package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import money.tegro.market.nft.NFTItem
import money.tegro.market.nft.NFTStubStandaloneItem
import mu.KLogging
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.smartcontract.wallet.v1.WalletV1R3
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.storeTlb

class MintStandaloneItemCommand : CliktCommand(name = "mint-standalone", help = "Mint a standalone item") {
    private val privateKey by option("--private-key", help = "Your wallet's private key (base64)").required()

    private val itemContent by option("--item-content", help = "Raw NFT item's content (base64)")
    private val itemOwner by option(
        "--item-owner",
        help = "Address of the account that will be set as an owner of the new item. By default it is your wallet address"
    )
    private val itemIndex by option("--item-index", help = "Index of the NFT item").long().default(0L)
    private val initAmount by option(
        "--init-amount",
        help = "Amount used to initialize an NFT item, in nanotons"
    ).long()
        .default(5_000_000L)

    override fun run() {
        runBlocking {
            val wallet = WalletV1R3(Tool.currentLiteApi, PrivateKeyEd25519(base64(privateKey)))
            println("Your wallet address is ${wallet.address().toString(userFriendly = true)}")

            println("Initializing stub NFT item")
            val stub = NFTStubStandaloneItem(
                owner = itemOwner?.let { MsgAddressIntStd(it) } ?: wallet.address(),
                individualContent = CellBuilder.createCell {
                    itemContent?.let { storeBytes(base64(it)) }
                },
                index = itemIndex
            )
            println("New NFT item address will be ${stub.address.toString(userFriendly = true)}")
            println("It will be owned by ${stub.owner.toString(userFriendly = true)}")

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
                                dest = stub.address,
                                value = CurrencyCollection(
                                    coins = Coins.ofNano(initAmount)
                                )
                            ),
                            init = stub.stateInit(),
                            body = Cell.of(),
                            storeBodyInRef = false
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

            println("Sending the initialization message")
            val result = Tool.currentLiteApi.sendMessage(
                Message(
                    CommonMsgInfo.ExtInMsgInfo(wallet.address()),
                    init = null,
                    body = body
                )
            )
            println("Result: $result")

            runBlocking { delay(10000L) }

            println("Checking if contract was correctly initialized")
            NFTItem.of(stub.address, Tool.currentLiteApi)?.run {
                println("Success! Item initialized:")
                println("\tAddress: ${address.toString(userFriendly = true)}")
                println("\tIndex: $index")
                println("\tOwner: ${owner.toString(userFriendly = true)}")
                println("\tContent: $individualContent")
            } ?: run {
                println("Something went wrong. Check your account address, try increasing the init amount")
            }
        }
    }

    companion object : KLogging()
}
