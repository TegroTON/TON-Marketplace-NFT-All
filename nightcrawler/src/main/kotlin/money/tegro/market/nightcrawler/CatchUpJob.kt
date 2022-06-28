package money.tegro.market.nightcrawler

import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.context.scope.refresh.RefreshEvent
import jakarta.inject.Singleton
import kotlinx.coroutines.runBlocking
import mu.KLogging
import java.time.Duration
import java.time.Instant

@Singleton
class CatchUpJob(
    private val context: ApplicationContext,
) {
    //    @Scheduled(initialDelay = "0s", fixedDelay = "60m")
    fun run() {
        runBlocking {
            logger.info { "Starting catching up" }
            val started = Instant.now()

            context.getEventPublisher(RefreshEvent::class.java)
                .publishEvent(RefreshEvent()) // To update the reference block

            context.getBean(LoadInitialCollectionsStep::class.java).run()
            context.getBean(CatchUpOnCollectionsStep::class.java).run()
            context.getBean(CatchUpOnItemsStep::class.java).run()

            logger.info { "Caught up in ${Duration.between(started, Instant.now())}" }
        }
    }


    companion object : KLogging()
}
