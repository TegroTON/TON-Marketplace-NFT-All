package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable
import java.time.Duration

@ConfigurationProperties("market.service")
interface ServiceConfig {
    @get:Bindable(defaultValue = "PT10M")
    val accountPeriod: Duration

    @get:Bindable(defaultValue = "PT10M")
    val collectionPeriod: Duration

    @get:Bindable(defaultValue = "PT10M")
    val itemPeriod: Duration

    @get:Bindable(defaultValue = "PT10M")
    val missingItemPeriod: Duration

    @get:Bindable(defaultValue = "PT10M")
    val royaltyPeriod: Duration

    @get:Bindable(defaultValue = "PT10M")
    val salePeriod: Duration
}
