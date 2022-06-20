package money.tegro.market.core.repository

import io.micronaut.data.annotation.Id
import java.time.Instant

interface RoyaltyRepository {
    fun update(
        @Id id: Long,
        numerator: Int?,
        denominator: Int?,
        destinationWorkchain: Int?,
        destinationAddress: ByteArray?,
        royaltyModified: Instant? = Instant.now(),
        royaltyUpdated: Instant? = Instant.now(),
    ): Unit
}
