package money.tegro.market.core.mapper

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.dto.ItemDTO
import money.tegro.market.core.model.AttributeModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.core.model.SaleModel
import money.tegro.market.core.toSafeBounceable
import org.ton.block.AddrNone
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Singleton
class ItemMapper(
    private val attributeMapper: AttributeMapper,
    private val royaltyMapper: RoyaltyMapper,
    private val saleMapper: SaleMapper,
) {
    fun map(
        item: ItemModel,
        attributes: Flux<AttributeModel> = Flux.empty(),
        royalty: Mono<RoyaltyModel> = Mono.empty(),
        sale: Mono<SaleModel> = Mono.empty(),
    ) = mono {
        ItemDTO(
            address = item.address.toSafeBounceable(),
            index = if (item.collection is AddrNone) null else item.index,
            collection = item.collection.toSafeBounceable(),
            owner = item.owner.toSafeBounceable(),
            name = item.name,
            description = item.description,
            attributes = attributes.flatMap(attributeMapper::map)
                .collectMap({ it.first }, { it.second })
                .awaitSingle(),
            sale = sale.flatMap(saleMapper::map).awaitSingleOrNull(),
            royalty = royalty.flatMap(royaltyMapper::map).awaitSingleOrNull(),
        )
    }
}
