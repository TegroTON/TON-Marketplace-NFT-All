package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.repository.CollectionRepository
import java.util.function.Consumer

@Singleton
class CollectionWriter(
    private val repository: CollectionRepository
) : Consumer<CollectionModel> {
    override fun accept(it: CollectionModel) {
        val id = it.id
        requireNotNull(id)
        repository.update(
            id,
            it.nextItemIndex,
            it.ownerWorkchain,
            it.ownerAddress,
            it.content,
            it.dataModified,
        )
    }
}
