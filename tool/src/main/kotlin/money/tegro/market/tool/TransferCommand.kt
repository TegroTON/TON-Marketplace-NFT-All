package money.tegro.market.tool

import jakarta.inject.Inject
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.core.dto.toSafeBounceable
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.bitstring.BitString
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.lite.api.LiteApi
import org.ton.smartcontract.wallet.v1.WalletV1R3
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.storeTlb
import picocli.CommandLine
import kotlin.system.exitProcess


@CommandLine.Command(name = "transfer", description = ["Transfer an item to another account"])
class TransferCommand(
) : Runnable {
    @Inject
    private lateinit var liteApi: LiteApi

    @Inject
    private lateinit var client: ItemClient

    @CommandLine.Option(names = ["--private-key"], description = ["Your wallet's private key (base64)"])
    private lateinit var privateKey: String

    @CommandLine.Option(names = ["--item"], description = ["Item that will be transferred"])
    private lateinit var itemAddress: String

    @CommandLine.Option(names = ["--destination"], description = ["Address that the item will be transferred to"])
    private lateinit var destinationAddress: String

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

            client.transferItem(
                item.address.toSafeBounceable(),
                wallet.address().toSafeBounceable(),
                MsgAddressIntStd(destinationAddress).toSafeBounceable(),
            ).awaitSingle().let {
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
                                        coins = Coins.ofNano(it.value)
                                    )
                                ),
                                init = null,
                                body = it.payload?.let { Cell.of(BitString.of(base64(it))) } ?: Cell.of(),
                                storeBodyInRef = true,
                            )
                        )
                    }
                }

                val signature = wallet.privateKey.sign(message.hash())

                println("Sending the message")
                liteApi.sendMessage(
                    Message(
                        CommonMsgInfo.ExtInMsgInfo(wallet.address()),
                        init = null,
                        body = CellBuilder.createCell {
                            storeBytes(signature)
                            storeBits(message.bits)
                            storeRefs(message.refs)
                        }
                    )
                )
            }
        }
    }
}
