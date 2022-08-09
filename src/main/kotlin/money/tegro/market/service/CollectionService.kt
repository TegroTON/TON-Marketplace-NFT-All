package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.runtime.event.annotation.EventListener
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import money.tegro.market.config.ServiceConfig
import money.tegro.market.contract.CollectionContract
import money.tegro.market.core.toRaw
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.repository.CollectionRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.api.exception.TvmException
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
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
        watchLiveJob.start()
    }

    @PreDestroy
    open fun onShutdown() {
        watchLiveJob.cancel()
    }

    suspend fun update(address: MsgAddressInt) {
        try {
            logger.debug("updating collection {}", kv("address", address.toRaw()))
            val data = CollectionContract.of(address as AddrStd, liteClient)
            val metadata = CollectionMetadata.of(data.content)

            collectionRepository.upsert(
                address = address,
                nextItemIndex = data.nextItemIndex,
                owner = data.owner,
                name = metadata.name,
                description = metadata.description,
                image = metadata.image
                    ?: metadata.imageData?.let { "data:image;base64," + base64(it) },
                coverImage = metadata.coverImage
                    ?: metadata.coverImageData?.let { "data:image;base64," + base64(it) },
            )
        } catch (e: TvmException) {
            logger.warn("could not get collection information for {}", kv("address", address.toRaw()), e)
        }
    }

    private val scheduledJob = launch {
        while (currentCoroutineContext().isActive) {
            logger.debug("running scheduled update of all database entities")
            collectionRepository.findAll().toList()
                .forEach { update(it.address) }

            kotlinx.coroutines.time.delay(config.collectionPeriod)
        }
    }

    private val watchLiveJob = launch {
        liveAccounts
            .filter { collectionRepository.existsById(it) }
            .collect {
                logger.info("{} matched database entity", kv("address", it.toRaw()))
                update(it)
            }
    }

    companion object : KLogging()
}
