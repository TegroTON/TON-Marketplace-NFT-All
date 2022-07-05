package money.tegro.market.nightcrawler

import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine

@CommandLine.Command(
    name = "nightcrawler",
)
class Application : Runnable {
    override fun run() {
        Thread.sleep(Long.MAX_VALUE)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            PicocliRunner.run(Application::class.java, *args)
        }
    }
}
