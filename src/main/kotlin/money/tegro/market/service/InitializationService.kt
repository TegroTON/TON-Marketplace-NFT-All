package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import money.tegro.market.contract.CollectionContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.model.CollectionModel
import money.tegro.market.repository.CollectionRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.client.LiteClient
import kotlin.coroutines.CoroutineContext

@Singleton
open class InitializationService(
    private val liteClient: LiteClient,
    private val resourceLoader: ClassPathResourceLoader,

    private val collectionRepository: CollectionRepository,
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    @EventListener
    open fun onStartup(event: StartupEvent) {
    }

    @PostConstruct
    open fun onInit() {
        job.start()
    }

    @PreDestroy
    open fun onShutdown() {
        job.cancel()
    }

    private val job = launch {
        logger.info("loading initial collections")
        resourceLoader.classLoader.getResourceAsStream("init_collections.csv")
            ?.reader()
            ?.readLines()
            .orEmpty()
            .asFlow()
            .filter { it.isNotBlank() }
            .map { AddrStd(it) }
            .filter { !collectionRepository.existsById(it) }
            .map {
                logger.debug("loading {}", kv("address", it.toSafeBounceable()))

                it to CollectionContract.of(it, liteClient)
            }
            .collect {
                collectionRepository.save(
                    CollectionModel(
                        address = it.first,
                        nextItemIndex = it.second.nextItemIndex,
                        owner = it.second.owner,
                        approved = true, // Those are assumed to be already manually checked
                    )
                )
            }
    }

    companion object : KLogging()
}
