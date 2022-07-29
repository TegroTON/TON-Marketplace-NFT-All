package money.tegro.market.service

import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import money.tegro.market.contract.CollectionContract
import money.tegro.market.core.toSafeBounceable
import money.tegro.market.model.CollectionModel
import money.tegro.market.repository.CollectionRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.ton.block.AddrStd
import org.ton.lite.client.LiteClient

@Singleton
class InitialCollectionService(
    private val liteClient: LiteClient,

    private val resourceLoader: ClassPathResourceLoader,
    private val collectionRepository: CollectionRepository,
) {
    @Scheduled(initialDelay = "0s")
    suspend fun setup() {
        logger.info("loading initial collections")
        resourceLoader.classLoader.getResourceAsStream("init_collections.csv")
            ?.reader()
            ?.readLines()
            .orEmpty()
            .asFlow()
            .filter { it.isNotBlank() }
            .map { AddrStd(it) }
            .filter { !collectionRepository.existsById(it) }
            .map {
                logger.debug("loading {}", kv("address", it.toSafeBounceable()))

                it to CollectionContract.of(it, liteClient)
            }
            .collect {
                collectionRepository.save(
                    CollectionModel(
                        address = it.first,
                        nextItemIndex = it.second.nextItemIndex,
                        owner = it.second.owner,
                        approved = true, // Those are assumed to be already manually checked
                    )
                )
            }
    }

    companion object : KLogging()
}
