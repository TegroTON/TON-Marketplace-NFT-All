package money.tegro.market.core.mapper

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.dto.CollectionDTO
import money.tegro.market.core.model.CollectionModel
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.core.toSafeBounceable
import reactor.core.publisher.Mono

@Singleton
class CollectionMapper(
    private val royaltyMapper: RoyaltyMapper,
) {
    fun map(
        collection: CollectionModel,
        itemNumber: Mono<Long> = Mono.empty(),
        royalty: Mono<RoyaltyModel> = Mono.empty(),
    ) = mono {
        CollectionDTO(
            address = collection.address.toSafeBounceable(),
            size = itemNumber.awaitSingleOrNull() ?: -1L,
            owner = collection.owner.toSafeBounceable(),
            name = collection.name,
            description = collection.description,
            royalty = royalty.flatMap(royaltyMapper::map).awaitSingleOrNull(),
        )
    }
}
