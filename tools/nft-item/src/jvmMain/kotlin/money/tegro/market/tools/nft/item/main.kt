package money.tegro.market.tools.nft.item

import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.Subcommand
import kotlinx.cli.default
import kotlinx.coroutines.runBlocking
import org.ton.block.MsgAddressInt
import org.ton.cell.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.cell.CellSlice
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
            .default("EQBVSrH770Egt4ykTNLons-5MupKVGYTBGwRvuMG0BxIxXpY")

        override fun execute() = runBlocking {
            val liteClient = LiteClient(liteServerHost, liteServerPort, hex(liteServerPubKey)).connect()

            val time = liteClient.getTime()
            println("[server time: $time] (${Instant.ofEpochSecond(time.now.toLong())})")

            val lastBlock = liteClient.getMasterchainInfo().last
            println("last block: $lastBlock")

            val addr = MsgAddressInt.AddrStd.parse(address)
            val accountId = LiteServerAccountId(addr.workchain_id, addr.address)

            val response = liteClient.runSmcMethod(
                0b100, // we only care about the result
                lastBlock,
                accountId,
                102351L, // get_nft_data
                BagOfCells(
                    CellBuilder.beginCell()
                        .storeUInt(0, 16)
                        .storeUInt(0, 8)
                        .endCell() // no parameters
                ).toByteArray()
            )
            require(response.exitCode == 0) { "Failed to run the method, exit code is ${response.exitCode}" }
            var loader = BagOfCells(response.result!!).roots.first().beginParse()
            loader.loadUInt(16) // skip whatever this is
            loader.loadUInt(8) // number of entries

            loader.loadUInt(8) // type of the last entry, going backwards here
            var next = loader.loadRef()
            val content = loader.loadRef()
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            var begin = loader.loadUInt(10).toInt()
            var end = loader.loadUInt(10).toInt()
            val owner = toAddress(Cell(loader.loadRef().bits.slice(begin..end)).beginParse())
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            begin = loader.loadUInt(10).toInt()
            end = loader.loadUInt(10).toInt()
            val collection = toAddress(Cell(loader.loadRef().bits.slice(begin..end)).beginParse())
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val index = loader.loadUInt(64).toInt()
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val initialized = loader.loadInt(64).toInt()
            println("NFT Item ${addr.toString(userFriendly = true)}:")
            println("\tInitialized: ${initialized == -1}")
            println("\tIndex: ${index}")
            println("\tCollection Address: ${collection.toString(userFriendly = true)}")
            println("\tOwner Address: ${owner.toString(userFriendly = true)}")
        }
    }

    val query = Query()

    parser.subcommands(query)

    parser.parse(args)
}

fun toAddress(slice: CellSlice): MsgAddressInt.AddrStd {
    slice.loadBits(3) // addr_std: 10 + 0 for no anycast
    return MsgAddressInt.AddrStd(
        null,
        slice.loadInt(8).toInt(),
        slice.loadBitString(256).toByteArray()
    )
}
