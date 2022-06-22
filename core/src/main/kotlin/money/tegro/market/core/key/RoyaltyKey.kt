package money.tegro.market.core.key

import io.micronaut.data.annotation.Embeddable
import io.micronaut.data.annotation.Relation

@Embeddable
data class RoyaltyKey(
    val numerator: Int,
    val denominator: Int,
    @Relation(Relation.Kind.EMBEDDED)
    val destination: AddressKey
)
