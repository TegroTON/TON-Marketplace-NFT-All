package money.tegro.market.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import money.tegro.market.contract.RoyaltyContract
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.core.toRaw
import money.tegro.market.repository.RoyaltyRepository
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
import org.ton.lite.client.LiteClient
import java.time.Instant
import kotlin.coroutines.CoroutineContext

@Service
class RoyaltyService(
    private val liteClient: LiteClient,

    private val liveAccounts: Flow<AddrStd>,

    private val royaltyRepository: RoyaltyRepository,
) : CoroutineScope, ApplicationListener<ApplicationStartedEvent>, InitializingBean, DisposableBean {
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    suspend fun update(address: MsgAddressInt): RoyaltyModel? = try {
        logger.debug("updating royalty {}", kv("address", address.toRaw()))
        RoyaltyContract.of(address as AddrStd, liteClient).let { royalty ->
            (royaltyRepository.findById(address) ?: RoyaltyModel(address)).run {
                royaltyRepository.save(
                    copy(
                        numerator = royalty.numerator,
                        denominator = royalty.denominator,
                        destination = royalty.destination,
                        updated = Instant.now()
                    )
                )
            }
        }
    } catch (e: TvmException) {
        logger.warn("could not get royalty for {}, removing entry", kv("address", address.toRaw()), e)
        royaltyRepository.deleteById(address)
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
            .filter { royaltyRepository.existsById(it) }
            .onEach {
                logger.info("{} matched database entity", kv("address", it.toRaw()))
            }
            .collect { update(it) }
    }

    companion object : KLogging()
}
