package money.tegro.market.core.mapper

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.dto.RoyaltyDTO
import money.tegro.market.core.model.RoyaltyModel
import money.tegro.market.core.toSafeBounceable
import reactor.core.publisher.Mono

@Singleton
class RoyaltyMapper {
    fun map(it: RoyaltyModel): Mono<RoyaltyDTO> = mono {
        RoyaltyDTO(
            value = it.numerator.toFloat() / it.denominator,
            destination = it.destination.toSafeBounceable(),
            // If it's AddrNone for some reason that's none of our business, this is purely for display purposes
        )
    }
}
