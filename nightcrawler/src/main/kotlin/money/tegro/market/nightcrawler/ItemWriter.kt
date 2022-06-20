package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.ItemRepository
import java.util.function.Consumer

@Singleton
class ItemWriter(
    private val repository: ItemRepository
) : Consumer<ItemModel> {
    override fun accept(it: ItemModel) {
        val id = it.id
        requireNotNull(id)
        repository.update(
            id,
            it.initialized,
            it.index,
            it.collection,
            it.ownerWorkchain,
            it.ownerAddress,
            it.content,
            it.dataModified,
        )
    }
}
