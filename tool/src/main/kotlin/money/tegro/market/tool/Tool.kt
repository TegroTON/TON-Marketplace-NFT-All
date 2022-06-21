package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import mu.KLogging
import org.ton.crypto.base64
import org.ton.lite.api.LiteApi

class Tool :
    CliktCommand(name = "tool", help = "Your one-stop NFT item/collection shop") {
    private val mainnetLiteApiOptions by MainnetLiteApiOptions()
    private val sandboxLiteApiOptions by SandboxLiteApiOptions()

    val sandbox by option(
        "--sandbox",
        help = "Work in sandbox instead of the mainnet. Default"
    ).flag("--mainnet", default = true)

    override fun run() {
        runBlocking {
            mainnetLiteApi =
                mainnetLiteApiOptions.let { ResilientLiteClient(it.host, it.port, base64(it.publicKey)) }.connect()
            sandboxLiteApi =
                sandboxLiteApiOptions.let { ResilientLiteClient(it.host, it.port, base64(it.publicKey)) }.connect()
            // TODO: Connect to the servers only when needed

            currentLiteApi = if (sandbox) sandboxLiteApi else mainnetLiteApi
        }
    }

    companion object : KLogging() {
        lateinit var mainnetLiteApi: LiteApi
        lateinit var sandboxLiteApi: LiteApi
        lateinit var currentLiteApi: LiteApi
    }
}
