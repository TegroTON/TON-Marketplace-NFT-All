package money.tegro.market.db

import kotlinx.datetime.Instant

sealed interface Royalty {
    var royaltyNumerator: Int?
    var royaltyDenominator: Int?

    var royaltyDestinationWorkchain: Int?
    var royaltyDestinationAddress: ByteArray?

    var royaltyLastIndexed: Instant?
}
