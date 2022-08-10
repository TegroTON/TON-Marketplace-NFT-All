package money.tegro.market.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.bind.DefaultValue

@ConstructorBinding
@ConfigurationProperties("market.liteapi")
class LiteApiConfig(
    @DefaultValue("822901272")
    val ipv4: Int,

    @DefaultValue("7811")
    val port: Int,

    @DefaultValue("eF2itktelj5g7nnJIaMW/RwAaE2Bzr1EMMrPAHDy3zA=")
    val key: String
)

