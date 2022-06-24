package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("money.tegro.market.nightcrawler")
class NightcrawlerConfiguration {
    var backpressureBufferSize = 69

    var collectionUpdatePeriod = Duration.ofMinutes(30)

    var missingItemsDiscoveryPeriod = Duration.ofMinutes(10)

    var dataUpdateThreshold = Duration.ofMinutes(30)
    var metadataUpdateThreshold = Duration.ofHours(12)
    var royaltyUpdateThreshold = Duration.ofHours(24)
}
