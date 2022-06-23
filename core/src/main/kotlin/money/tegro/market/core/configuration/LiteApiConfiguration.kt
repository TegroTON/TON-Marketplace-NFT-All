package money.tegro.market.core.configuration

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("money.tegro.market.liteapi")
class LiteApiConfiguration {
    var ipv4 = 1091947910
    var port = 7496
    var key = "EI32HF4Lr9mKSnw/dqiXQabpydo/FsyAPSwoeav4lbI="
}
