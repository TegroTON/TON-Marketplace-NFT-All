package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.runtime.event.annotation.EventListener
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.runBlocking
import money.tegro.market.contract.CollectionContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.model.CollectionModel
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.CollectionRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.extra.bool.not

@Singleton
open class InitialCollectionService(
    private val config: ServiceConfig,

    private val liteApi: LiteApi,

    private val resourceLoader: ClassPathResourceLoader,
    private val collectionRepository: CollectionRepository,
) {
    @Async
    @EventListener
    open fun setup(event: StartupEvent) {
        runBlocking {
            logger.info("loading initial collections")
            resourceLoader.classLoader.getResourceAsStream("init_collections.csv")
                ?.reader()
                ?.readLines()
                .orEmpty()
                .toFlux()
                .filter { it.trim().length == 0 }
                .map { AddrStd(it) }
                .filterWhen { collectionRepository.existsById(it).not() }
                .concatMap {
                    mono {
                        logger.debug("loading {}", kv("address", it.toSafeBounceable()))

                        it to CollectionContract.of(it, liteApi)
                    }
                }
                .subscribe {
                    collectionRepository.save(
                        CollectionModel(
                            address = it.first,
                            nextItemIndex = it.second.nextItemIndex,
                            owner = it.second.owner,
                            approved = true, // Those are assumed to be already manually checked
                        )
                    ).subscribe()
                }
        }
    }

    companion object : KLogging()
}
