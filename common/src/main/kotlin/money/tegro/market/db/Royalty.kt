package money.tegro.market.db

import java.time.Instant

sealed interface Royalty {
    val royaltyNumerator: Int?
    val royaltyDenominator: Int?
    val royaltyDestinationWorkchain: Int?
    val royaltyDestinationAddress: ByteArray?
    val royaltyLastIndexed: Instant?
}
