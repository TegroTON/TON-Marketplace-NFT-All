package money.tegro.market.tool

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int

interface LiteApiOptions {
    val host: Int
    val port: Int
    val publicKey: String
}

class MainnetLiteApiOptions : OptionGroup("[MAINNET] lite API options"), LiteApiOptions {
    override val host by option(
        "--lite-server-mainnet-host",
        help = "Lite server host IP address",
        envvar = "LITE_SERVER_MAINNET_HOST"
    )
        .int()
        .default(908566172)
    override val port by option(
        "--lite-server-mainnet-port",
        help = "Lite server port number",
        envvar = "LITE_SERVER_MAINNET_PORT"
    )
        .int()
        .default(51565)
    override val publicKey by option(
        "--lite-server-mainnet-public-key",
        help = "Lite server public key (base64)",
        envvar = "LITE_SERVER_MAINNET_PUBLIC_KEY"
    )
        .default("TDg+ILLlRugRB4Kpg3wXjPcoc+d+Eeb7kuVe16CS9z8=")
}

class SandboxLiteApiOptions : OptionGroup("[SANDBOX] lite API options"), LiteApiOptions {
    override val host by option(
        "--lite-server-sandbox-host",
        help = "Lite server host IP address",
        envvar = "LITE_SERVER_SANDBOX_HOST"
    )
        .int()
        .default(1426768764)
    override val port by option(
        "--lite-server-sandbox-port",
        help = "Lite server port number",
        envvar = "LITE_SERVER_SANDBOX_PORT"
    )
        .int()
        .default(13724)
    override val publicKey by option(
        "--lite-server-sandbox-public-key",
        help = "Lite server public key (base64)",
        envvar = "LITE_SERVER_SANDBOX_PUBLIC_KEY"
    )
        .default("R1KsqYlNks2Zows+I9s4ywhilbSevs9dH1x2KF9MeSU=")
}
