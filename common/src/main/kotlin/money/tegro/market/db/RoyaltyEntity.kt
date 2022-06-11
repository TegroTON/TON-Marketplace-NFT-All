package money.tegro.market.db

interface RoyaltyEntity {
    val royaltyNumerator: Int?
    val royaltyDenominator: Int?
    val royaltyDestinationWorkchain: Int?
    val royaltyDestinationAddress: ByteArray?
}
