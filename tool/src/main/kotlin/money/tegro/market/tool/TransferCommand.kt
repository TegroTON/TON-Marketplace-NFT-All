package money.tegro.market.tool

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTItem
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.lite.api.LiteApi
import org.ton.smartcontract.wallet.v1.WalletV1R3
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.constructor.tlbCodec
import org.ton.tlb.storeTlb
import picocli.CommandLine
import kotlin.system.exitProcess


@CommandLine.Command(name = "transfer", description = ["Transfer an item to another account"])
class TransferCommand(
) : Runnable {
    private lateinit var liteApi: LiteApi

    @CommandLine.Parameters(description = ["Addresses of the items to query"])
    private lateinit var addresses: List<String>

    @CommandLine.Option(names = ["--private-key"], description = ["Your wallet's private key (base64)"])
    private lateinit var privateKey: String

    @CommandLine.Option(names = ["--item"], description = ["Item that will be transferred"])
    private lateinit var itemAddress: String

    @CommandLine.Option(names = ["--destination"], description = ["Address that the item will be transferred to"])
    private lateinit var destinationAddress: String

    @CommandLine.Option(
        names = ["--response"],
        description = ["Address to send confirmation to, defaults to your wallet's address"]
    )
    private var responseAddress: String? = null

    @CommandLine.Option(
        names = ["--payload"],
        description = ["Extra base64-encoded payload that will be sent to the response address"]
    )
    private var payload: String? = null

    @CommandLine.Option(
        names = ["--send"],
        description = ["Amount of nanotons that will be sent to the NFT item contract"]
    )
    private var sendAmount: Long = 100_000_000L

    @CommandLine.Option(names = ["--forward"], description = ["Amount of nanotons that will be sent to the new owners"])
    private var forwardAmount: Long = 10_000_000L

    @CommandLine.Option(names = ["--query-id"], description = ["Query id"])
    private var queryId: Long = 0L

    override fun run() {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
            val wallet = WalletV1R3(liteApi, PrivateKeyEd25519(base64(privateKey)))
            println("Your wallet address is ${wallet.address().toString(userFriendly = true)}")

            println("Querying item ${MsgAddressIntStd(itemAddress).toString(userFriendly = true)} information")
            val item = NFTItem.of(MsgAddressIntStd(itemAddress), liteApi) ?: run {
                println("No such item, quitting")
                exitProcess(-1)
            }

            if (item.owner != wallet.address()) {
                println("Item owner address (${item.owner.toString(userFriendly = true)}) differs from provided address")
                println("Cannot proceed, quitting")
                exitProcess(-1)
            }

            val newOwner = MsgAddressIntStd(destinationAddress)
            println(
                "Preparing a message to the item with a request to transfer item to ${newOwner.toString(userFriendly = true)}"
            )
            val message = wallet.createSigningMessage(wallet.seqno()) {
                storeUInt(3, 8) // send mode
                storeRef {
                    storeTlb(
                        MessageRelaxed.tlbCodec(AnyTlbConstructor), MessageRelaxed(
                            info = CommonMsgInfoRelaxed.IntMsgInfo(
                                ihrDisabled = true,
                                bounce = true,
                                bounced = false,
                                src = MsgAddressExtNone,
                                dest = item.address,
                                value = CurrencyCollection(
                                    coins = Coins.ofNano(sendAmount)
                                )
                            ),
                            init = null,
                            body = CellBuilder.createCell {
                                storeUInt(0x5fcc3d14, 32) // OP, transfer
                                storeUInt(queryId, 64) // Query id
                                storeTlb(MsgAddress.tlbCodec(), newOwner) // new owner
                                storeTlb(
                                    MsgAddress.tlbCodec(),
                                    responseAddress?.let { MsgAddressIntStd(it) } ?: wallet.address()
                                ) // response destination
                                // in_msg_body~load_int(1); ;; this nft don't use custom_payload
                                // bruh moment
                                storeInt(0, 1)
                                storeTlb(Coins.tlbCodec(), Coins.ofNano(forwardAmount))
                                storeTlb(
                                    Either.tlbCodec(
                                        Cell.tlbCodec(), Cell.tlbCodec()
                                    ),
                                    Either.of(
                                        null,
                                        CellBuilder.createCell {
                                            payload?.let { storeBytes(base64(it)) }
                                        }
                                    )
                                )
                            },
                            storeBodyInRef = true,
                        )
                    )
                }
            }

            val signature = wallet.privateKey.sign(message.hash())
            val body = CellBuilder.createCell {
                storeBytes(signature)
                storeBits(message.bits)
                storeRefs(message.refs)
            }

            println("Sending the message")
            val result = liteApi.sendMessage(
                Message(
                    CommonMsgInfo.ExtInMsgInfo(wallet.address()),
                    init = null,
                    body = body
                )
            )
            println("Result: $result")

            runBlocking { delay(10000L) }

            println("Checking if item was correctly transferred")
            val itemUpdated = NFTItem.of(item.address, liteApi)
            println("Tranfer ${item.owner.toString(userFriendly = true)} -> ${itemUpdated?.owner?.toString(userFriendly = true)}")
            if (itemUpdated != null && itemUpdated.owner == newOwner) {
                println("Huge success!")
            } else {
                println("Something went wrong")
                exitProcess(-1)
            }
        }
    }
}
