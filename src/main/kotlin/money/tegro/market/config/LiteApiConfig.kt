package money.tegro.market.core.configuration

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("market.liteapi")
interface LiteApiConfig {
    @get:Bindable(defaultValue = "\${LITEAPI_IPV4:1091947910}")
    val ipv4: Int

    @get:Bindable(defaultValue = "\${LITEAPI_PORT:7496}")
    val port: Int

    @get:Bindable(defaultValue = "\${LITEAPI_KEY:`EI32HF4Lr9mKSnw/dqiXQabpydo/FsyAPSwoeav4lbI=`}")
    val key: String
}

