package money.tegro.market.core.configuration

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("market.liteclient")
interface LiteClientConfig {
    @get:Bindable(defaultValue = "\${LITECLIENT_IPV4:1091947910}")
    val ipv4: Int

    @get:Bindable(defaultValue = "\${LITECLIENT_PORT:7496}")
    val port: Int

    @get:Bindable(defaultValue = "\${LITECLIENT_KEY:`EI32HF4Lr9mKSnw/dqiXQabpydo/FsyAPSwoeav4lbI=`}")
    val key: String
}

