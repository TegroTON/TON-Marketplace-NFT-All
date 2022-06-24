package money.tegro.market.nightcrawler.writer

import jakarta.inject.Singleton
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.MetadataModel
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.MetadataRepository
import java.util.function.Consumer

open class MetadataWriter<M : MetadataModel, R : MetadataRepository>(
    private val repository: R
) : Consumer<M> {
    open fun extraMetadata(it: M) {}

    override fun accept(it: M) {
        repository.update(
            it.address,
            it.name,
            it.description,
            it.image,
            it.imageData,
            it.coverImage,
            it.coverImageData,
        )

        extraMetadata(it)
    }
}

@Singleton
class CollectionMetadataWriter(repository: CollectionRepository) :
    MetadataWriter<CollectionModel, CollectionRepository>(repository)

@Singleton
class ItemMetadataWriter(repository: ItemRepository) : MetadataWriter<ItemModel, ItemRepository>(repository)
