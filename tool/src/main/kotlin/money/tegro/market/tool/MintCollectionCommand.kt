package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.blockchain.nft.NFTRoyalty
import money.tegro.market.blockchain.nft.NFTStubCollection
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.smartcontract.wallet.v1.WalletV1R3
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.storeTlb

class MintCollectionCommand : CliktCommand(name = "mint", help = "Mint a new NFT collection") {
    private val privateKey by option("--private-key", help = "Your wallet's private key (base64)").required()

    private val collectionContent by option(
        "--collection-content",
        help = "Collection content, raw collection-specific content (base64)"
    )
    private val commonContent by option(
        "--common-content",
        help = "Common content, a string that will be concatenated with item content"
    )
    private val collectionOwner by option(
        "--collection-owner",
        help = "Address of the account that will be set as an owner of the new collection. By default it is your wallet address"
    )
    private val collectionRoyaltyNumerator by option(
        "--collection-royalty-numerator",
        help = "Collection royalty numerator, a number that is devided by denominator to find royalty percentage"
    ).int()
    private val collectionRoyaltyDenominator by option(
        "--collection-royalty-denominator",
        help = "Collection royalty denominator, a number that devides numerator to find royalty percentage"
    ).int()
    private val collectionRoyaltyDestination by option(
        "--collection-royalty-destination",
        help = "Collection royalty destination, an address where royalty will be sent. By default it's your wallet address"
    )
    private val initAmount by option(
        "--init-amount",
        help = "Amount used to initialize an NFT collection, in nanotons"
    ).long()
        .default(5_000_000L)

    override fun run() {
        runBlocking {
            val wallet = WalletV1R3(Tool.currentLiteApi, PrivateKeyEd25519(base64(privateKey)))
            println("Your wallet address is ${wallet.address().toString(userFriendly = true)}")

            println("Initializing stub NFT collection")
            val stub = NFTStubCollection(
                owner = collectionOwner?.let { MsgAddressIntStd(it) } ?: wallet.address(),
                collectionContent = CellBuilder.createCell {
                    collectionContent?.let { storeBytes(it.toByteArray()) }
                },
                commonContent = CellBuilder.createCell {
                    commonContent?.let { storeBytes(it.toByteArray()) }
                },
                royalty = collectionRoyaltyDestination?.let {
                    val destination = MsgAddressIntStd(it)
                    collectionRoyaltyDenominator?.let { denominator ->
                        collectionRoyaltyNumerator?.let { numerator ->
                            NFTRoyalty(numerator, denominator, destination)
                        }
                    }
                }
            )
            println("New NFT collection address will be ${stub.address.toString(userFriendly = true)}")
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
            NFTCollection.of(stub.address, Tool.currentLiteApi).run {
                println("Success! Collection initialized:")
                println("\tAddress: ${address.toString(userFriendly = true)}")
                println("\tNext item index: $nextItemIndex")
                println("\tOwner: ${owner.toString(userFriendly = true)}")
                println("\tContent: $content")

                println("Checking its royalty parameters:")
                NFTRoyalty.of(address, Tool.currentLiteApi)?.run {
                    println("\tRoyalty percentage: ${value().times(100.0)}%")
                    println("\tRoyalty destination: ${destination.toString(userFriendly = true)}")
                } ?: run {
                    println("\tNo royalty information")
                }
            }
        }
    }
}
