package money.tegro.market.tool

import jakarta.inject.Inject
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.core.dto.toSafeBounceable
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.AddrStd
import org.ton.crypto.base64
import org.ton.lite.api.LiteApi
import org.ton.smartcontract.wallet.v1.WalletV1R3
import picocli.CommandLine
import kotlin.system.exitProcess


@CommandLine.Command(name = "transfer", description = ["Transfer an item to another account"])
class TransferCommand : Runnable {
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
            val wallet = WalletV1R3(liteApi, PrivateKeyEd25519(base64(privateKey)))
            println("Your wallet address is ${wallet.address().toString(userFriendly = true)}")

            println("Querying item ${AddrStd(itemAddress).toString(userFriendly = true)} information")
            val item = NFTItem.of(AddrStd(itemAddress), liteApi) ?: run {
                println("No such item, quitting")
                exitProcess(-1)
            }

            if (item.owner != wallet.address()) {
                println("Item owner address (${(item.owner as AddrStd).toString(userFriendly = true)}) differs from provided address")
                println("Cannot proceed, quitting")
                exitProcess(-1)
            }

            client.transferItem(
                item.address.toSafeBounceable()!!,
                AddrStd(destinationAddress).toSafeBounceable(),
                wallet.address().toSafeBounceable(),
            ).awaitSingle().performTransaction(wallet, liteApi)
        }
    }
}
