package money.tegro.market.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.bind.DefaultValue
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties("market.service")
class ServiceConfig(
    @DefaultValue("PT1H")
    val accountPeriod: Duration,

    @DefaultValue("PT1H")
    val collectionPeriod: Duration,

    @DefaultValue("PT1H")
    val itemPeriod: Duration,

    @DefaultValue("PT10M")
    val royaltyPeriod: Duration,

    @DefaultValue("PT10M")
    val salePeriod: Duration,
)
