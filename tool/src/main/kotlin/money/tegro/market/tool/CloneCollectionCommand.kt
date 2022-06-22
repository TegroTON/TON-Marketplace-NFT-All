package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.nft.*
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.bitstring.BitString
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.smartcontract.wallet.v1.WalletV1R3
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.storeTlb

class CloneCollectionCommand : CliktCommand(name = "clone", help = "Clone a mainnet collection to the sandbox") {
    private val privateKey by option("--private-key", help = "Your wallet's private key (base64)").required()

    private val collection by argument(
        "ollection-content",
        help = "Mainnet collection address"
    )
    private val skipCollection by option(
        "--skip-collection",
        help = "Skip creation of the collection (if it is already initialized) and start minting items"
    ).flag(default = false)
    private val collectionInitAmount by option(
        "--collection-init-amount",
        help = "Amount used to initialize an NFT collection, in nanotons"
    ).long()
        .default(1_000_000_000L)
    private val itemInitAmount by option(
        "--item-init-amount",
        help = "Amount used to initialize each item in the NFT collection, in nanotons"
    ).long()
        .default(50_000_000L)

    override fun run() {
        runBlocking {
            val wallet = WalletV1R3(Tool.sandboxLiteApi, PrivateKeyEd25519(base64(privateKey)))
            println("Your wallet address is ${wallet.address().toString(userFriendly = true)}")

            println("Getting original collection information")
            val original = NFTCollection.of(MsgAddressIntStd(collection), Tool.mainnetLiteApi)
            val originalRoyalty = NFTRoyalty.of(MsgAddressIntStd(collection), Tool.mainnetLiteApi)
            val randomItem = original.item((0 until original.nextItemIndex).random(), Tool.mainnetLiteApi)!!
            val randomContent = randomItem.content(Tool.mainnetLiteApi)
            val content = randomContent.bits.toByteArray().drop(1).plus(
                randomContent.treeWalk().map { it.bits.toByteArray() }
                    .reduceOrNull { acc, bytes -> acc + bytes }
                    ?.toList() ?: listOf()).toByteArray().let { Cell.of(BitString(it)) }

            println("Content" + content.toString())

            println("Initializing stub NFT collection")
            val stub = NFTStubCollection(
                owner = wallet.address(),
                collectionContent = original.content,
                commonContent = content, // No way to easy get it, so we just use one item's full content instead
                royalty = originalRoyalty?.run { NFTRoyalty(numerator, denominator, wallet.address()) }
            )

            println("New NFT collection address will be ${stub.address.toString(userFriendly = true)}")

            if (!skipCollection) {
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
                                        coins = Coins.ofNano(collectionInitAmount)
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
                val result = Tool.sandboxLiteApi.sendMessage(
                    Message(
                        CommonMsgInfo.ExtInMsgInfo(wallet.address()),
                        init = null,
                        body = body
                    )
                )
                println("Result: $result")

                runBlocking { delay(20_000L) }

                println("Checking if contract was correctly initialized")
                NFTCollection.of(stub.address, Tool.sandboxLiteApi).run {
                    println("Success! Collection initialized:")
                    println("\tAddress: ${address.toString(userFriendly = true)}")
                    println("\tNext item index: $nextItemIndex")
                    println("\tOwner: ${owner.toString(userFriendly = true)}")
                    println("\tContent: $content")

                    println("Checking its royalty parameters:")
                    NFTRoyalty.of(address, Tool.sandboxLiteApi)?.run {
                        println("\tRoyalty percentage: ${value().times(100.0)}%")
                        println("\tRoyalty destination: ${destination.toString(userFriendly = true)}")
                    } ?: run {
                        println("\tNo royalty information")
                    }
                }
            }

            println("Initializing all ${original.nextItemIndex} collection items")
            for (index in 0 until original.nextItemIndex) {
                var successful = false
                println("Sending a message to the collection with a request to mint item no. $index")
                do {
                    wallet.transfer(
                        dest = stub.address,
                        bounce = true,
                        Coins.ofNano(itemInitAmount),
                        wallet.seqno(),
                        CellBuilder.createCell {
                            storeUInt(1, 32) // OP, mint
                            storeUInt(0, 64) // Query id
                            storeUInt(index, 64) // New item index
                            storeTlb(Coins.tlbCodec(), Coins.ofNano(itemInitAmount))
                            storeRef {// Body that is sent to the item
                                storeTlb(MsgAddress.tlbCodec(), wallet.address())
                                storeRef {
                                    Cell.of()
                                } // content
                            }
                        },
                    )
                    delay(10_000L) // Delay just in case

                    try {
                        val itemAddress = NFTDeployedCollection.itemAddressOf(stub.address, index, Tool.sandboxLiteApi)
                        val item = NFTItem.of(itemAddress, Tool.sandboxLiteApi)
                        if (item == null) {
                            println("Something's wrong, I can feel it. Trying again")
                            successful = false
                        } else {
                            println("Success! Item no. ${item.index} is ${item.address.toString(userFriendly = true)}")
                            successful = true
                        }
                    } catch (e: Exception) {
                        successful = false
                        println("This doesnt seem to have worked, trying again: $e")
                    }
                } while (!successful)
            }
        }
    }
}
