package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.RoyaltyRepository
import java.util.function.Consumer

open class RoyaltyWriter<R : RoyaltyRepository>(
    private val repository: R
) : Consumer<RoyaltyModel> {
    override fun accept(it: RoyaltyModel) {
        repository.update(
            it.address,
            it.royalty,
            it.royaltyModified,
        )
    }
}

@Singleton
class CollectionRoyaltyWriter(repository: CollectionRepository) : RoyaltyWriter<CollectionRepository>(repository)

@Singleton
class ItemRoyaltyWriter(repository: ItemRepository) : RoyaltyWriter<ItemRepository>(repository)
