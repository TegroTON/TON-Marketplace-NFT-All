package money.tegro.market.core.configuration

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("market.liteapi")
interface LiteApiConfig {
    @get:Bindable(defaultValue = "\${LITEAPI_IPV4:822901272}")
    val ipv4: Int

    @get:Bindable(defaultValue = "\${LITEAPI_PORT:7811}")
    val port: Int

    @get:Bindable(defaultValue = "\${LITEAPI_KEY:`eF2itktelj5g7nnJIaMW/RwAaE2Bzr1EMMrPAHDy3zA=`}")
    val key: String
}

