package money.tegro.market.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import money.tegro.market.contract.CollectionContract
import money.tegro.market.contract.ItemContract
import money.tegro.market.core.toRaw
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.model.ItemModel
import money.tegro.market.repository.ItemRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.stereotype.Service
import org.ton.api.exception.TvmException
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient
import java.time.Instant
import kotlin.coroutines.CoroutineContext

@Service
class ItemService(
    private val liteClient: LiteClient,

    private val liveAccounts: Flow<AddrStd>,

    private val itemRepository: ItemRepository,
) : CoroutineScope, ApplicationListener<ApplicationStartedEvent>, InitializingBean, DisposableBean {
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    suspend fun update(address: MsgAddressInt): ItemModel? = try {
        logger.debug("updating item {}", kv("address", address.toRaw()))
        val data = ItemContract.of(address as AddrStd, liteClient)
        val metadata = ItemMetadata.of(
            (data.collection as? AddrStd)
                ?.let { CollectionContract.itemContent(it, data.index, data.individualContent, liteClient) }
                ?: data.individualContent
        )

        (itemRepository.findById(address) ?: ItemModel(address)).run {
            itemRepository.save(
                copy(
                    initialized = data.initialized,
                    index = data.index,
                    collection = data.collection,
                    owner = data.owner,
                    name = metadata.name,
                    description = metadata.description,
                    image = metadata.image
                        ?: metadata.imageData?.let { "data:image;base64," + base64(it) },
                    attributes = metadata.attributes.orEmpty().associate { it.trait to it.value },
                    updated = Instant.now(),
                )
            )
        }
    } catch (e: TvmException) {
        logger.warn("could not get item information for {}", kv("address", address.toRaw()), e)
        null
    }

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
    }

    override fun afterPropertiesSet() {
        liveJob.start()
    }

    override fun destroy() {
        liveJob.cancel()
    }

    private val liveJob = launch {
        liveAccounts
            .filter { itemRepository.existsById(it) }
            .onEach {
                logger.info("{} matched database entity", kv("address", it.toRaw()))
            }
            .collect { update(it) }
    }

    companion object : KLogging()
}
