package money.tegro.market.core.repository

import io.micronaut.data.annotation.Id
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.key.RoyaltyKey
import java.time.Instant

interface RoyaltyRepository {
    fun update(
        @Id address: AddressKey,
        royalty: RoyaltyKey?,
        royaltyModified: Instant = Instant.now(),
        royaltyUpdated: Instant = Instant.now(),
    ): Unit
}
