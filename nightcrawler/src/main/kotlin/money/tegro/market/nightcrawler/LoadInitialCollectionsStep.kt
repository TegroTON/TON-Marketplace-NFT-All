package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Prototype
import io.micronaut.core.io.scan.ClassPathResourceLoader
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.repository.CollectionRepository
import mu.KLogging
import org.ton.block.AddrStd
import reactor.kotlin.core.publisher.toFlux

@Prototype
class LoadInitialCollectionsStep(
    private val resourceLoader: ClassPathResourceLoader,
    private val collectionRepository: CollectionRepository,
) {
    fun run() {
        logger.info { "Loading initial collections" }

        resourceLoader.classLoader.getResource("init_collections.csv")?.readText()?.let {
            it.lineSequence()
                .toFlux()
                .filter { it.isNotBlank() }
                .map { AddrStd(it) }
                .filterWhen { collectionRepository.existsByAddress(it).map { !it } }
                .concatMap {
                    collectionRepository.save(CollectionModel(it))
                }
                .blockLast()
        } ?: run {
            CatchUpJob.logger.debug { "No file with initial collections found in the classpath" }
        }
    }

    companion object : KLogging()
}
