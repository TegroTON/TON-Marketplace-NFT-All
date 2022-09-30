package money.tegro.market.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.bind.DefaultValue
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties("market.cache")
data class CacheProperties(
    @DefaultValue("PT48H")
    val collectionMetadataExpires: Duration,

    @DefaultValue("PT48H")
    val itemMetadataExpires: Duration,
)
