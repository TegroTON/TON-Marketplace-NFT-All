package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.blockchain.nft.NFTDeployedCollectionItem
import money.tegro.market.blockchain.nft.NFTItem
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.Coins
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressIntStd
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.smartcontract.wallet.v1.WalletV1R3
import org.ton.tlb.storeTlb
import kotlin.system.exitProcess

class MintCollectionItemCommand :
    CliktCommand(name = "mint-collection", help = "Mint an item in an already existing collection") {
    private val privateKey by option("--private-key", help = "Your wallet's private key (base64)").required()
    private val collectionAddress by option("--collection", help = "Target collection address").required()

    private val itemContent by option(
        "--item-content",
        help = "Item content, a string that will be concatenated with collection's common_content"
    )
    private val itemIndex by option(
        "--item-index",
        help = "Index of the NFT item. By default uses collection's next_item_index"
    ).long()
    private val itemOwner by option(
        "--item-owner",
        help = "Address of the account that will be set as an owner of the new item. By default it is your wallet address"
    )
    private val initAmount by option(
        "--init-amount",
        help = "Amount used to initialize an NFT item, in nanotons"
    ).long()
        .default(50_000_000L)
    private val sendAmount by option(
        "--send-amount",
        help = "Amount that is sent to the NFT collection contract, in nanotons"
    ).long()
        .default(100_000_000L)
    private val queryId by option("--query-id", help = "Querry ID of the outbound message").long().default(0L)

    override fun run() {
        runBlocking {
            val wallet = WalletV1R3(Tool.currentLiteApi, PrivateKeyEd25519(base64(privateKey)))
            println("Your wallet address is ${wallet.address().toString(userFriendly = true)}")

            println("Querying collection ${MsgAddressIntStd(collectionAddress).toString(userFriendly = true)} information")
            val collection = NFTCollection.of(MsgAddressIntStd(collectionAddress), Tool.currentLiteApi)

            if (collection.owner != wallet.address()) {
                println("Collection owner address (${collection.owner.toString(userFriendly = true)}) differs from provided address")
                println("Cannot proceed, quitting")
                exitProcess(-1)
            }

            val index = itemIndex ?: collection.nextItemIndex
            val owner = itemOwner?.let { MsgAddressIntStd(it) } ?: wallet.address()

            println("Sending a message to the collection with a request to mint item no. $index")
            println("New item will be owned by ${owner.toString(userFriendly = true)}")
            wallet.transfer(
                dest = collection.address,
                bounce = true,
                Coins.ofNano(sendAmount),
                wallet.seqno(),
                CellBuilder.createCell {
                    storeUInt(1, 32) // OP, mint
                    storeUInt(queryId, 64) // Query id
                    storeUInt(index, 64) // New item index
                    storeTlb(Coins.tlbCodec(), Coins.ofNano(initAmount))
                    storeRef {// Body that is sent to the
                        storeTlb(MsgAddress.tlbCodec(), owner)
                        storeRef {
                            itemContent?.let { storeBytes(it.toByteArray()) }
                        } // content
                    }
                },
            )

            runBlocking { delay(10000L) }

            println("Checking if item was correctly initialized")
            val itemAddress = collection.itemAddress(index, Tool.currentLiteApi)
            println("New item with the index $index address is ${itemAddress.toString(userFriendly = true)}")

            NFTItem.of(itemAddress, Tool.currentLiteApi)?.run {
                println("Success! Item initialized:")
                println("\tAddress: ${address.toString(userFriendly = true)}")
                println("\tIndex: $index")
                println("\tCollection: ${(this as? NFTDeployedCollectionItem)?.collection?.toString(userFriendly = true)}")
                println("\tOwner: ${owner.toString(userFriendly = true)}")
                println("\tContent: $individualContent")
            } ?: run {
                println("Something went wrong. Check your account address, try increasing the init amount")
            }
        }
    }
}
