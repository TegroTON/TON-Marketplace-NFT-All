package money.tegro.market.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import money.tegro.market.core.toRaw
import money.tegro.market.model.collections
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ktorm.database.Database
import org.ktorm.dsl.eq
import org.ktorm.entity.any
import org.springframework.beans.factory.DisposableBean
import org.springframework.boot.context.event.ApplicationStartedEvent
import org.springframework.context.ApplicationListener
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Service
import org.ton.block.AddrStd
import kotlin.coroutines.CoroutineContext

@Service
class InitializationService(
    private val resourceLoader: ResourceLoader,

    private val database: Database,
    private val collectionService: CollectionService,
) : CoroutineScope, ApplicationListener<ApplicationStartedEvent>, DisposableBean {
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    override fun onApplicationEvent(event: ApplicationStartedEvent) {
        job.start()
    }

    override fun destroy() {
        job.cancel()
    }

    private val job = launch {
        logger.info("loading initial collections")
        resourceLoader.classLoader
            ?.getResourceAsStream("init_collections.csv")
            ?.reader()
            ?.readLines()
            .orEmpty()
            .asFlow()
            .filter { it.isNotBlank() }
            .map { AddrStd(it) }
            .filterNot { database.collections.any { a -> a.address eq it } }
            .collect {
                logger.debug("loading {}", kv("address", it.toRaw()))
                collectionService.update(it)
            }
    }

    companion object : KLogging()
}
