package money.tegro.market.nightcrawler

import money.tegro.market.ton.ResilientLiteClient
import org.springframework.beans.factory.config.AbstractFactoryBean
import org.ton.lite.api.LiteApi

class LiteApiFactory : AbstractFactoryBean<LiteApi>() {
    private lateinit var liteApi: LiteApi

    val lastMasterchainBlock = suspend { liteApi.getMasterchainInfo().last }

    override fun getObjectType() = LiteApi::class.java

    override fun createInstance() = liteApi

    suspend fun setConnectionParameters(ipv4: Int, port: Int, publicKey: ByteArray) {
        liteApi = ResilientLiteClient(ipv4, port, publicKey)
        (liteApi as ResilientLiteClient).connect()
    }
}
