package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import money.tegro.market.config.ServiceConfig
import money.tegro.market.contract.CollectionContract
import money.tegro.market.contract.ItemContract
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.toRaw
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.repository.ItemRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.api.exception.TvmException
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
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

    suspend fun get(address: MsgAddressInt): ItemModel? = itemRepository.findById(address) ?: upsert(address)

    suspend fun upsert(address: MsgAddressInt): ItemModel? = try {
        logger.debug("updating item {}", kv("address", address.toRaw()))
        val data = ItemContract.of(address as AddrStd, liteClient)
        val metadata = ItemMetadata.of(
            (data.collection as? AddrStd)
                ?.let { CollectionContract.itemContent(it, data.index, data.individualContent, liteClient) }
                ?: data.individualContent
        )

        itemRepository.upsert(
            address = address,
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
    } catch (e: TvmException) {
        logger.warn("could not get item information for {}", kv("address", address.toRaw()), e)
        null
    }

    @EventListener
    open fun onStartup(event: StartupEvent) {
    }

    @PostConstruct
    open fun onInit() {
        watchLiveJob.start()
    }

    @PreDestroy
    open fun onShutdown() {
        watchLiveJob.cancel()
    }

    private val watchLiveJob = launch {
        liveAccounts
            .filter { itemRepository.existsById(it) }
            .onEach {
                logger.info("{} matched database entity", kv("address", it.toRaw()))
            }
            .collect { upsert(it) }
    }

    companion object : KLogging()
}
