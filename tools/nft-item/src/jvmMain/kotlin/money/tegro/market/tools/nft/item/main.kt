package money.tegro.market.tools.nft.item

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import org.ton.adnl.AdnlPublicKey
import org.ton.adnl.AdnlTcpClient
import org.ton.adnl.AdnlTcpClientImpl
import org.ton.cell.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.crypto.hex
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import java.time.Instant

suspend fun main() = coroutineScope {
    val liteClient = LiteClient(
        host = "67.207.74.182",
        port = 4924,
        publicKey = hex("a5e253c3f6ab9517ecb204ee7fd04cca9273a8e8bb49712a48f496884c365353")
    ).connect()
    val time = liteClient.getTime()
    println("[server time: $time] (${Instant.ofEpochSecond(time.now.toLong())})")

    val lastBlock = liteClient.getMasterchainInfo().last
    println("last block: $lastBlock")
    val accountId = LiteServerAccountId(0, hex("83dfd552e63729b472fcbcc8c45ebcc6691702558b68ec7527e1ba403a0f31a8"))

    val result = liteClient.runSmcMethod(
        0,
        lastBlock,
        accountId,
        85143L, //  seqno
        byteArrayOf() // takes no parameters
    )
    println(result)
}

class LiteClient(
    val adnlTcpClient: AdnlTcpClient
) : LiteApi {
    constructor(
        host: String,
        port: Int,
        publicKey: ByteArray
    ) : this(AdnlTcpClientImpl(host, port, AdnlPublicKey(publicKey), Dispatchers.Default))

    suspend fun connect() = apply {
        adnlTcpClient.connect()
    }

    override suspend fun sendRawQuery(byteArray: ByteArray): ByteArray =
        adnlTcpClient.sendQuery(byteArray)
}
