package money.tegro.market.nightcrawler

import io.micronaut.core.io.scan.ClassPathResourceLoader
import io.micronaut.scheduling.annotation.Scheduled
import jakarta.inject.Singleton
import money.tegro.market.core.model.CollectionData
import mu.KLogging
import org.ton.block.MsgAddressIntStd

@Singleton
class InitializeCollections(
    private val resourceLoader: ClassPathResourceLoader,
    private val collectionRepository: CollectionRepository,
) {
    @Scheduled(initialDelay = "0s")
    fun run() {
        logger.info { "Loading initial collections" }

        resourceLoader.classLoader.getResource("init_collections.csv")?.readText()?.let {
            it.lineSequence()
                .filter { it.isNotBlank() }
                .map { MsgAddressIntStd(it) }
                .filter { !collectionRepository.existsByAddressStd(it) }
                .forEach {
                    collectionRepository.save(CollectionData(it)).subscribe()
                }
        } ?: run {
            logger.debug { "No file with initial collections found in the classpath" }
        }
    }

    companion object : KLogging()
}
