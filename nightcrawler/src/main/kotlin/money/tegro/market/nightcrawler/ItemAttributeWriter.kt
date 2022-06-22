package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import money.tegro.market.core.model.ItemAttributeModel
import money.tegro.market.core.repository.ItemAttributeRepository
import java.util.function.Consumer

@Singleton
class ItemAttributeWriter(
    private val repository: ItemAttributeRepository
) : Consumer<ItemAttributeModel> {
    override fun accept(it: ItemAttributeModel) {
        repository.findByItemAndTrait(it.item, it.trait).block()?.let { attribute ->
            val id = attribute.id
            requireNotNull(id)
            repository.update(id, it.value)
        } ?: run {
            repository.save(it).subscribe()
        }
    }
}
