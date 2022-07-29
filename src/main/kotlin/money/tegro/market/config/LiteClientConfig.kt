package money.tegro.market.core.configuration

import io.micronaut.context.annotation.ConfigurationProperties
import io.micronaut.core.bind.annotation.Bindable

@ConfigurationProperties("market.liteclient")
interface LiteClientConfig {
    @get:Bindable(defaultValue = "\${LITECLIENT_IPV4:1959448750}")
    val ipv4: Int

    @get:Bindable(defaultValue = "\${LITECLIENT_PORT:51281}")
    val port: Int

    @get:Bindable(defaultValue = "\${LITECLIENT_KEY:`hyXd2d6yyiD/wirjoraSrKek1jYhOyzbQoIzV85CB98=`}")
    val key: String
}

