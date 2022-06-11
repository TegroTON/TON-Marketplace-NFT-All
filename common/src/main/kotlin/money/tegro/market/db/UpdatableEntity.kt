package money.tegro.market.db

import java.time.Instant

interface UpdatableEntity {
    val discovered: Instant
    val updated: Instant?
    val modified: Instant?
}
