package money.tegro.market.nightcrawler.writer

import jakarta.inject.Singleton
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.repository.CollectionRepository
import java.util.function.Consumer

@Singleton
class CollectionWriter(
    private val repository: CollectionRepository
) : Consumer<CollectionModel> {
    override fun accept(it: CollectionModel) {
        repository.update(
            it.address,
            it.nextItemIndex,
            it.owner,
            it.content,
            it.modified,
        )
    }
}
