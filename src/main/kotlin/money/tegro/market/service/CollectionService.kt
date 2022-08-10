package money.tegro.market.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import money.tegro.market.contract.CollectionContract
import money.tegro.market.core.toRaw
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.model.CollectionModel
import money.tegro.market.repository.CollectionRepository
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
class CollectionService(
    private val liteClient: LiteClient,

    private val liveAccounts: Flow<AddrStd>,

    private val collectionRepository: CollectionRepository,
) : CoroutineScope, ApplicationListener<ApplicationStartedEvent>, InitializingBean, DisposableBean {
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    suspend fun update(address: MsgAddressInt): CollectionModel? =
        try {
            logger.debug("updating collection {}", kv("address", address.toRaw()))
            val data = CollectionContract.of(address as AddrStd, liteClient)
            val metadata = CollectionMetadata.of(data.content)

            (collectionRepository.findById(address) ?: CollectionModel(address)).run {
                collectionRepository.save(
                    copy(
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
                )
            }
        } catch (e: TvmException) {
            logger.warn("could not get collection information for {}", kv("address", address.toRaw()), e)
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
            .filter { collectionRepository.existsById(it) }
            .collect {
                logger.info("{} matched database entity", kv("address", it.toRaw()))
                update(it)
            }
    }

    companion object : KLogging()
}
