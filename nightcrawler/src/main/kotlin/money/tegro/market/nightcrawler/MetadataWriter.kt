package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.MetadataModel
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemAttributeRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.MetadataRepository
import java.util.function.Consumer

open class MetadataWriter<M : MetadataModel, R : MetadataRepository>(
    private val repository: R
) : Consumer<M> {
    open fun extraMetadata(it: M) {}

    override fun accept(it: M) {
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

        extraMetadata(it)
    }
}

@Singleton
class CollectionMetadataWriter(repository: CollectionRepository) :
    MetadataWriter<CollectionModel, CollectionRepository>(repository)

@Singleton
class ItemMetadataWriter(itemRepository: ItemRepository, private val attributeRepository: ItemAttributeRepository) :
    MetadataWriter<ItemModel, ItemRepository>(itemRepository) {
    override fun extraMetadata(item: ItemModel) {
        item.attributes.orEmpty()
            .forEach { attribute ->
                attributeRepository.findByItemAndTrait(item, attribute.trait).block()?.let {
                    val id = it.id
                    requireNotNull(id)
                    attributeRepository.update(id, attribute.trait, attribute.value)
                } ?: run {
                    attributeRepository.save(attribute).subscribe()
                }
            }

        super.extraMetadata(item)
    }
}
