package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import money.tegro.market.core.model.MetadataModel
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.MetadataRepository
import java.util.function.Consumer

open class MetadataWriter<R : MetadataRepository>(
    private val repository: R
) : Consumer<MetadataModel> {
    override fun accept(it: MetadataModel) {
        val id = it.id
        requireNotNull(id)
        repository.update(
            id,
            it.name,
            it.description,
            it.image,
            it.imageData,
            it.coverImage,
            it.coverImageData,
        )
    }
}

@Singleton
class CollectionMetadataWriter(repository: CollectionRepository) : MetadataWriter<CollectionRepository>(repository)

@Singleton
class ItemMetadataWriter(repository: ItemRepository) : MetadataWriter<ItemRepository>(repository)
