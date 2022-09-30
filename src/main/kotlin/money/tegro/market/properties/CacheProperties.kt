package money.tegro.market.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import kotlin.time.Duration

@ConfigurationProperties("market.cache")
class CacheProperties {
    var collectionMetadataExpires: Duration = Duration.parse("PT48H")

    var itemMetadataExpires: Duration = Duration.parse("PT48H")
}
