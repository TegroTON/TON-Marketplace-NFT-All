package money.tegro.market.service

import io.micronaut.context.event.StartupEvent
import io.micronaut.data.model.Sort
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import jakarta.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import money.tegro.market.config.ServiceConfig
import money.tegro.market.contract.RoyaltyContract
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.core.toRaw
import money.tegro.market.repository.RoyaltyRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.api.exception.TvmException
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient
import kotlin.coroutines.CoroutineContext

@Singleton
open class RoyaltyService(
    private val liteClient: LiteClient,
    private val config: ServiceConfig,

    private val liveAccounts: Flow<AddrStd>,

    private val royaltyRepository: RoyaltyRepository,
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    suspend fun get(address: MsgAddressInt): RoyaltyModel? = royaltyRepository.findById(address) ?: update(address)

    suspend fun update(address: MsgAddressInt): RoyaltyModel? = try {
        logger.debug("updating royalty {}", kv("address", address.toRaw()))
        RoyaltyContract.of(address as AddrStd, liteClient).let {
            royaltyRepository.upsert(
                address,
                it.numerator,
                it.denominator,
                it.destination
            )
        }
    } catch (e: TvmException) {
        logger.warn("could not get royalty for {}, removing entry", kv("address", address.toRaw()), e)
        royaltyRepository.deleteById(address)
        null
    }

    //    @EventListener
    open fun onStartup(event: StartupEvent) {
    }

    @PostConstruct
    open fun onInit() {
        scheduledJob.start()
        watchLiveJob.start()
    }

    @PreDestroy
    open fun onShutdown() {
        scheduledJob.cancel()
        watchLiveJob.cancel()
    }

    private val scheduledJob = launch {
        while (currentCoroutineContext().isActive) {
            logger.debug("running scheduled update of all database entities")
            royaltyRepository.findAll(Sort.of(Sort.Order.asc("updated")))
                .collect { update(it.address) }

            kotlinx.coroutines.time.delay(config.royaltyPeriod)
        }
    }

    private val watchLiveJob = launch {
        liveAccounts
            .filter { royaltyRepository.existsById(it) }
            .onEach {
                logger.info("{} matched database entity", kv("address", it.toRaw()))
            }
            .collect { update(it) }
    }

    companion object : KLogging()
}
