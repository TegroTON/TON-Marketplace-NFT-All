package money.tegro.market.config

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable
import java.time.Duration

@ConfigurationProperties("market.service")
interface ServiceConfig {
    @get:Bindable(defaultValue = "PT1H")
    val accountPeriod: Duration

    @get:Bindable(defaultValue = "PT1H")
    val collectionPeriod: Duration

    @get:Bindable(defaultValue = "PT1H")
    val itemPeriod: Duration

    @get:Bindable(defaultValue = "PT1H")
    val missingItemPeriod: Duration

    @get:Bindable(defaultValue = "PT10M")
    val royaltyPeriod: Duration

    @get:Bindable(defaultValue = "PT10M")
    val salePeriod: Duration
}
