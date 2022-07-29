package money.tegro.market.service

import io.micronaut.data.model.Sort
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.time.delay
import money.tegro.market.contract.CollectionContract
import money.tegro.market.contract.ItemContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.nightcrawler.ServiceConfig
import money.tegro.market.repository.AttributeRepository
import money.tegro.market.repository.ItemRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient
import java.time.Instant

@Singleton
class ItemService(
    private val config: ServiceConfig,

    private val liteClient: LiteClient,
    private val liveAccounts: Flow<AddrStd>,

    private val attributeRepository: AttributeRepository,
    private val itemRepository: ItemRepository,
) {
    @Scheduled(initialDelay = "0s")
    suspend fun setup() {
        merge(
            // Watch live
            liveAccounts
                .mapNotNull { itemRepository.findById(it) }
                .onEach {
                    logger.info("{} matched database entity", kv("address", it.address.toSafeBounceable()))
                },
            // Apart from watching live interactions, update them periodically
            flow {
                while (currentCoroutineContext().isActive) {
                    logger.debug("running scheduled update of all database entities")
                    emitAll(itemRepository.findAll(Sort.of(Sort.Order.asc("updated"))))
                    delay(config.itemPeriod)
                }
            }
        )
            .map {
                logger.debug("updating item {}", kv("address", it.address.toSafeBounceable()))
                val data = ItemContract.of(it.address, liteClient)
                val metadata = ItemMetadata.of(
                    (data.collection as? AddrStd)
                        ?.let { CollectionContract.itemContent(it, data.index, data.individualContent, liteClient) }
                        ?: data.individualContent
                )

                metadata.attributes.orEmpty()
                    .forEach { attribute ->
                        attributeRepository.upsert(it.address, attribute.trait, attribute.value)
                    }

                it.copy(
                    initialized = data.initialized,
                    index = data.index,
                    collection = data.collection,
                    owner = data.owner,
                    name = metadata.name,
                    description = metadata.description,
                    image = metadata.image
                        ?: metadata.imageData?.let { "data:image;base64," + base64(it) },
                    updated = Instant.now(),
                )
            }
            .collect { itemRepository.update(it) }
    }

    companion object : KLogging()
}
