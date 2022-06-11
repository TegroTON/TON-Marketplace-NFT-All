package money.tegro.market.db

interface RoyaltyEntity {
    val numerator: Int?
    val denominator: Int?
    val destinationWorkchain: Int?
    val destinationAddress: ByteArray?
}
