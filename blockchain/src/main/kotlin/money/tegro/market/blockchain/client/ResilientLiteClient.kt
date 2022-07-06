package money.tegro.market.blockchain.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import mu.KLogging
import mu.withLoggingContext
import org.ton.adnl.AdnlPublicKey
import org.ton.adnl.AdnlTcpClient
import org.ton.adnl.AdnlTcpClientImpl
import org.ton.lite.client.LiteClient
import org.ton.tl.TlCodec

open class ResilientLiteClient(
    adnlTcpClient: AdnlTcpClient,
    private val maxAttempts: Int = 100,
    private val retryMillis: Long = 100L,
) : LiteClient(adnlTcpClient) {
    constructor(ipv4: Int, port: Int, publicKey: ByteArray) : this(
        AdnlTcpClientImpl(org.ton.adnl.ipv4(ipv4), port, AdnlPublicKey(publicKey), Dispatchers.Default)
    )

    override suspend fun sendRawQuery(byteArray: ByteArray): ByteArray {
        var attempt = 1
        while (true) {
            try {
                return super.sendRawQuery(byteArray)
            } catch (e: Exception) {
                withLoggingContext("attempt" to attempt.toString()) {
                    if (e is ClosedReceiveChannelException) {
                        Companion.logger.warn { "connection dropped? reconnecting" }
                        this.connect()
                    }

                    if (attempt >= maxAttempts) {
                        Companion.logger.warn { "too many attempts, giving up" }
                        throw e
                    }

                    Companion.logger.debug { "attempt failed, trying again" }
                    delay(retryMillis)
                    attempt += 1
                }
            }
        }
    }

    override suspend fun <Q : Any, A : Any> query(query: Q, queryCodec: TlCodec<Q>, answerCodec: TlCodec<A>): A {
        var attempt = 1
        while (true) {
            withLoggingContext(
                "host" to adnlTcpClient.host,
                "port" to adnlTcpClient.port.toString(),
            ) {
                try {
                    withLoggingContext("query" to query.toString()) {
                        logger.debug { "performing query" }
                        return@query super.query(query, queryCodec, answerCodec)
                    }
                } catch (e: Exception) {
                    withLoggingContext("attempt" to attempt.toString()) {
                        if (attempt >= maxAttempts) {
                            Companion.logger.info { "too many attempts, giving up" }
                            throw e
                        }

                        Companion.logger.debug { "attempt failed, trying again" }
                        delay(retryMillis)
                        attempt += 1
                    }
                }
            }
        }
    }

    companion object : KLogging()
}

