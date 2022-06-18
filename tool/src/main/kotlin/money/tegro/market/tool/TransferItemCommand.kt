package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import money.tegro.market.nft.NFTItem
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.smartcontract.wallet.v1.WalletV1R3
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.constructor.tlbCodec
import org.ton.tlb.storeTlb
import kotlin.system.exitProcess

class TransferItemCommand :
    CliktCommand(name = "transfer", help = "Transfer an item to another account") {
    private val privateKey by option("--private-key", help = "Your wallet's private key (base64)").required()
    private val itemAddress by option("--item", help = "Item that is to be transferred").required()

    private val newOwnerAddress by option("--new-owner", help = "New owner's address").required()

    private val responseDestination by option(
        "--response-destination",
        help = "Address to send response confirmation to, defaults to your wallet's address"
    )
    private val forwardPayload by option(
        "--forward-payload",
        help = "Base64-encoded payload to be sent to the response destination alongside the confirmation"
    )
    private val sendAmount by option(
        "--send-amount",
        help = "Amount that is sent to the NFT item contract, in nanotons"
    ).long()
        .default(100_000_000L)
    private val forwardAmount by option(
        "--forward-amount",
        help = "Amount that is sent to the new owner, in nanotons"
    ).long()
        .default(10_000_000L)

    private val queryId by option("--query-id", help = "Querry ID of the outbound message").long().default(0L)

    override fun run() {
        runBlocking {
            val wallet = WalletV1R3(Tool.currentLiteApi, PrivateKeyEd25519(base64(privateKey)))
            println("Your wallet address is ${wallet.address().toString(userFriendly = true)}")

            println("Querying item ${MsgAddressIntStd(itemAddress).toString(userFriendly = true)} information")
            val item = NFTItem.of(MsgAddressIntStd(itemAddress), Tool.currentLiteApi) ?: run {
                println("No such item, quitting")
                exitProcess(-1)
            }

            if (item.owner != wallet.address()) {
                println("Item owner address (${item.owner.toString(userFriendly = true)}) differs from provided address")
                println("Cannot proceed, quitting")
                exitProcess(-1)
            }

            val newOwner = MsgAddressIntStd(newOwnerAddress)
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
                                bounce = false,
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
                                    responseDestination?.let { MsgAddressIntStd(it) } ?: wallet.address()
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
                                            forwardPayload?.let { storeBytes(base64(it)) }
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
            val result = Tool.currentLiteApi.sendMessage(
                Message(
                    CommonMsgInfo.ExtInMsgInfo(wallet.address()),
                    init = null,
                    body = body
                )
            )
            println("Result: $result")

            runBlocking { delay(10000L) }

            println("Checking if item was correctly transferred")
            val itemUpdated = NFTItem.of(item.address, Tool.currentLiteApi)
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
