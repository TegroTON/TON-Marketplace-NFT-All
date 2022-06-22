package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties("nighcrawler.update.items")
class UpdateDatabaseItemsConfiguration {
    var dataUpdateThreshold = Duration.ofMinutes(30)
    var metadataUpdateThreshold = Duration.ofHours(12)
    var royaltyUpdateThreshold = Duration.ofHours(24)
}
