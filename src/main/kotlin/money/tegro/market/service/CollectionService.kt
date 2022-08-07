package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.time.delay
import money.tegro.market.config.ServiceConfig
import money.tegro.market.contract.CollectionContract
import money.tegro.market.core.toRaw
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.repository.CollectionRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient
import kotlin.coroutines.CoroutineContext

@Singleton
open class CollectionService(
    private val liteClient: LiteClient,
    private val config: ServiceConfig,

    private val liveAccounts: Flow<AddrStd>,

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
        merge(
            // Watch live
            liveAccounts
                .mapNotNull { collectionRepository.findById(it) }
                .onEach {
                    logger.info("{} matched database entity", kv("address", it.address.toRaw()))
                },
            // Apart from watching live interactions, update them periodically
            channelFlow {
                while (currentCoroutineContext().isActive) {
                    AccountService.logger.debug("running scheduled update of all database entities")
                    collectionRepository.findAll().collect { send(it) }
                    delay(config.collectionPeriod)
                }
            }
        )
            .collect {
                logger.debug("updating collection {}", kv("address", it.address.toRaw()))
                val data = CollectionContract.of(it.address as AddrStd, liteClient)
                val metadata = CollectionMetadata.of(data.content)

                collectionRepository.update(
                    it.copy(
                        address = it.address,
                        nextItemIndex = data.nextItemIndex,
                        owner = data.owner,
                        name = metadata.name,
                        description = metadata.description,
                        image = metadata.image
                            ?: metadata.imageData?.let { "data:image;base64," + base64(it) },
                        coverImage = metadata.coverImage
                            ?: metadata.coverImageData?.let { "data:image;base64," + base64(it) },
                    )
                )
            }
    }

    companion object : KLogging()
}
