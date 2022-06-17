package money.tegro.market.ton

import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.config.AbstractFactoryBean
import org.ton.lite.api.LiteApi
import java.util.concurrent.atomic.AtomicInteger

class LiteApiFactory(val ipv4: Int, val port: Int, val publicKey: ByteArray, private val size: Int = 8) :
    AbstractFactoryBean<LiteApi>() {
    private var liteApis: MutableList<LiteApi> = mutableListOf()
    private var current = AtomicInteger(0)

    init {
        isSingleton = false
        runBlocking {
            while (liteApis.size < size)
                liteApis.add(
                    ResilientLiteClient(ipv4, port, publicKey).connect()
                )
        }
    }

    override fun getObjectType() = LiteApi::class.java

    override fun createInstance(): LiteApi {
        println("HIT")
        current.compareAndExchange(size, 0)

        return liteApis.get(
            current.getAndIncrement()
        )
    }
}
