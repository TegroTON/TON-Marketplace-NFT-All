package money.tegro.market.tool

import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine

@CommandLine.Command(
    name = "tool",
    subcommands = [QueryItemCommand::class, QueryCollectionCommand::class, TransferCommand::class, SellCommand::class]
)
class Application : Runnable {
    override fun run() {
        TODO("Not yet implemented")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PicocliRunner.run(Application::class.java, *args)
        }
    }
}
