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
import money.tegro.market.contract.ItemContract
import money.tegro.market.core.toRaw
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.repository.ItemRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient
import kotlin.coroutines.CoroutineContext

@Singleton
open class ItemService(
    private val liteClient: LiteClient,
    private val config: ServiceConfig,

    private val liveAccounts: Flow<AddrStd>,

    private val itemRepository: ItemRepository,
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
                .mapNotNull { itemRepository.findById(it) }
                .onEach {
                    logger.info("{} matched database entity", kv("address", it.address.toRaw()))
                },
            // Apart from watching live interactions, update them periodically
            channelFlow {
                while (currentCoroutineContext().isActive) {
                    logger.debug("running scheduled update of all database entities")
                    itemRepository.findAll().collect { send(it) }
                    delay(config.itemPeriod)
                }
            }
        )
            .collect {
                logger.debug("updating item {}", kv("address", it.address.toRaw()))
                val data = ItemContract.of(it.address as AddrStd, liteClient)
                val metadata = ItemMetadata.of(
                    (data.collection as? AddrStd)
                        ?.let { CollectionContract.itemContent(it, data.index, data.individualContent, liteClient) }
                        ?: data.individualContent
                )

                itemRepository.update(
                    address = it.address,
                    initialized = data.initialized,
                    index = data.index,
                    collection = data.collection,
                    owner = data.owner,
                    name = metadata.name,
                    description = metadata.description,
                    image = metadata.image
                        ?: metadata.imageData?.let { "data:image;base64," + base64(it) },
                    attributes = metadata.attributes.orEmpty().associate { it.trait to it.value },
                )
            }
    }

    companion object : KLogging()
}
