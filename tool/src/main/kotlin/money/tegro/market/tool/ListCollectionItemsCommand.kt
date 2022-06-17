package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import kotlinx.coroutines.runBlocking
import money.tegro.market.nft.NFTCollection
import org.ton.block.MsgAddressIntStd

class ListCollectionItemsCommand :
    CliktCommand(name = "list", help = "List collection items") {
    private val addresses by argument(
        name = "addresses",
        help = "Addresses of the collections to query"
    ).multiple(required = true)

    override fun run() {
        runBlocking {
            for (addressStr in addresses) {
                val address = MsgAddressIntStd(addressStr)
                NFTCollection.of(address, Tool.currentLiteApi)
                    .itemAddresses(Tool.currentLiteApi)
                    .collect { println(it.toString(userFriendly = true)) }
            }
        }
    }
}
