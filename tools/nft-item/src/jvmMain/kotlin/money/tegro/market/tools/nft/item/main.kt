package money.tegro.market.tools.nft.item

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.coroutines.runBlocking
import org.ton.cell.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.crypto.hex
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import java.time.Instant

suspend fun main(args: Array<String>) {
    var parser = ArgParser("nft-item")

    val liteServerHost by parser.option(ArgType.String, "host", "o ", "Lite server host IP address")
        .default("67.207.74.182")
    val liteServerPort by parser.option(ArgType.Int, "port", "p", "Lite server port number").default(4924)
    val liteServerPubKey by parser.option(ArgType.String, "pubkey", "k", "Lite server public key")
        .default("a5e253c3f6ab9517ecb204ee7fd04cca9273a8e8bb49712a48f496884c365353")

    class Query : Subcommand("query", "Query NFT item info") {
        val address by option(ArgType.String, "address", "a", "NFT item contract address")
            .default("0:83dfd552e63729b472fcbcc8c45ebcc6691702558b68ec7527e1ba403a0f31a8")

        override fun execute() = runBlocking {
            val liteClient = LiteClient(liteServerHost, liteServerPort, hex(liteServerPubKey)).connect()

            val time = liteClient.getTime()
            println("[server time: $time] (${Instant.ofEpochSecond(time.now.toLong())})")

            val lastBlock = liteClient.getMasterchainInfo().last
            println("last block: $lastBlock")

            val wc = address.split(":").first().toInt()
            val addr = address.split(":").last()
            val accountId = LiteServerAccountId(wc, hex(addr))

            val result = liteClient.runSmcMethod(
                0,
                lastBlock,
                accountId,
                "seqno",
                BagOfCells(CellBuilder.beginCell().storeUInt(0, 16).storeUInt(0, 8).endCell())
            )
            println(result)
        }
    }

    val query = Query()

    parser.subcommands(query)

    parser.parse(args)
}
