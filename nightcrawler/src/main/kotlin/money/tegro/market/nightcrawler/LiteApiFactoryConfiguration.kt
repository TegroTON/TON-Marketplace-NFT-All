package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("ton.liteapi")
class LiteApiFactoryConfiguration {
    var ipv4 = 1091947910
    var port = 7496
    var key = "EI32HF4Lr9mKSnw/dqiXQabpydo/FsyAPSwoeav4lbI="
}
