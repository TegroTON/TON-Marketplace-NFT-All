package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("money.tegro.market.nightcrawler")
class NightcrawlerConfiguration {
    var catchUpPeriod: Duration = Duration.ofMinutes(60)

    var accountUpdatePeriod: Duration = Duration.ofDays(69)

    var collectionUpdatePeriod: Duration = Duration.ofMinutes(10)
    var collectionMetadataUpdatePeriod: Duration = Duration.ofHours(1)

    var itemUpdatePeriod: Duration = Duration.ofMinutes(1)
    var itemMetadataUpdatePeriod: Duration = Duration.ofHours(1)

    var royaltyUpdatePeriod: Duration = Duration.ofHours(1)

    var saleUpdatePeriod: Duration = Duration.ofMinutes(1)
}
