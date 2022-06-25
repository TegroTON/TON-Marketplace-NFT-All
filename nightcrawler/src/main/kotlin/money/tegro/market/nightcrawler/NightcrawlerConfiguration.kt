package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("money.tegro.market.nightcrawler")
class NightcrawlerConfiguration {
    var backpressureBufferSize = 69

    var collectionUpdatePeriod: Duration = Duration.ofMinutes(30)

    var itemUpdatePeriod: Duration = Duration.ofMinutes(1)

    var missingItemsDiscoveryPeriod: Duration = Duration.ofMinutes(10)

    var dataUpdateThreshold: Duration = Duration.ofMinutes(30)
    var metadataUpdateThreshold: Duration = Duration.ofHours(12)
    var royaltyUpdateThreshold: Duration = Duration.ofHours(24)
}
