package money.tegro.market.core.model

import money.tegro.market.core.key.RoyaltyKey
import java.time.Instant

interface RoyaltyModel : BasicModel {
    var royalty: RoyaltyKey?

    var royaltyUpdated: Instant
    var royaltyModified: Instant
}
