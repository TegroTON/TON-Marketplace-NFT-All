package money.tegro.market.blockchain.nft

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import mu.KLogging
import org.ton.cell.Cell
import org.ton.smartcontract.SnakeData
import org.ton.smartcontract.SnakeDataCons
import org.ton.smartcontract.SnakeDataTail
import org.ton.tlb.loadTlb

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class NFTMetadata : Addressable {
    abstract val name: String?
    abstract val description: String?
    abstract val image: String?
    abstract val imageData: ByteArray?

    companion object : KLogging() {
        // TODO: Full snakedata
        private val snakeDataCodec by lazy { SnakeDataTail.tlbCodec() }

        @JvmStatic
        suspend fun parseContent(
            content: Cell,
            httpClient: HttpClient
        ): String {
            val cs = content.beginParse()
            return when (val contentLayout = cs.loadUInt(8).toInt()) {
                0x00 -> {
                    logger.debug { "on-chain content layout detected" }
                    TODO("on-chain content layout, really?")
                }
                0x01 -> {
                    val rawData = cs.loadTlb(snakeDataCodec)
                    cs.endParse()

                    val url = String(rawData.toByteArray())
                    logger.debug { "off-chain content layout, url is: $url" }

                    val a = httpClient.get(url)
                    logger.debug { a }
                    a.bodyAsText()
                }
                else -> {
                    throw Error("unknown content layout $contentLayout, can't proceed")
                }
            }
        }
    }
}

fun SnakeData.toByteArray(): ByteArray = when (this) {
    is SnakeDataCons -> bits.toByteArray() + next.toByteArray()
    is SnakeDataTail -> bits.toByteArray()
}
