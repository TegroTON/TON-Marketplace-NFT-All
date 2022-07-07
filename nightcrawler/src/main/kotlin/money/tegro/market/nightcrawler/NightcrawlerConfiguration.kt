package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("money.tegro.market.nightcrawler")
class NightcrawlerConfiguration {
    var catchUpPeriod = Duration.ofMinutes(60)

    var accountUpdatePeriod = Duration.ofDays(69)

    var collectionUpdatePeriod = Duration.ofMinutes(10)
    var collectionMetadataUpdatePeriod = Duration.ofHours(1)

    var itemUpdatePeriod = Duration.ofMinutes(1)
    var itemMetadataUpdatePeriod = Duration.ofHours(1)

    var royaltyUpdatePeriod = Duration.ofHours(1)

    var saleUpdatePeriod = Duration.ofMinutes(1)
}
