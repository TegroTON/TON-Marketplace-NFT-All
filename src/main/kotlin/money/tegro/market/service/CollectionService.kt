package money.tegro.market.service

import io.micronaut.context.event.ApplicationEventListener
import io.micronaut.context.event.StartupEvent
import io.micronaut.data.model.Sort
import io.micronaut.scheduling.annotation.Async
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.time.delay
import money.tegro.market.contract.CollectionContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.CollectionRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient
import java.time.Instant

@Singleton
open class CollectionService(
    private val config: ServiceConfig,

    private val liteClient: LiteClient,
    private val liveAccounts: Flow<AddrStd>,

    private val collectionRepository: CollectionRepository,
) : ApplicationEventListener<StartupEvent> {
    @Async
    open override fun onApplicationEvent(event: StartupEvent?) {
        runBlocking(Dispatchers.Default) {
            merge(
                // Watch live
                liveAccounts
                    .mapNotNull { collectionRepository.findById(it) }
                    .onEach {
                        logger.info("{} matched database entity", kv("address", it.address.toSafeBounceable()))
                    },
                // Apart from watching live interactions, update them periodically
                flow {
                    while (currentCoroutineContext().isActive) {
                        logger.debug("running scheduled update of all database entities")
                        emitAll(collectionRepository.findAll(Sort.of(Sort.Order.asc("updated"))))
                        delay(config.collectionPeriod)
                    }
                }
            )
                .map {
                    logger.debug("updating collection {}", kv("address", it.address.toSafeBounceable()))
                    val data = CollectionContract.of(it.address, liteClient)
                    val metadata = CollectionMetadata.of(data.content)

                    it.copy(
                        nextItemIndex = data.nextItemIndex,
                        owner = data.owner,
                        name = metadata.name,
                        description = metadata.description,
                        image = metadata.image
                            ?: metadata.imageData?.let { "data:image;base64," + base64(it) },
                        coverImage = metadata.coverImage
                            ?: metadata.coverImageData?.let { "data:image;base64," + base64(it) },
                        updated = Instant.now(),
                    )
                }
                .flowOn(Dispatchers.Default)
                .collect { collectionRepository.update(it) }
        }
    }

    companion object : KLogging()
}
