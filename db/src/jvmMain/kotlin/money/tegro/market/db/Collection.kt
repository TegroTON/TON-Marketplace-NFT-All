package money.tegro.market.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.ton.block.MsgAddressInt

class Collection(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Collection>(Collections) {
        @JvmStatic
        fun find(collection: MsgAddressInt.AddrStd) =
            this.find { (Collections.workchain eq collection.workchainId) and (Collections.address eq collection.address) }
    }

    private var rawWorkchain by Collections.workchain
    private var rawAddress by Collections.address

    var address: MsgAddressInt.AddrStd
        get() = MsgAddressInt.AddrStd(rawWorkchain, rawAddress)
        set(value) {
            rawWorkchain = value.workchainId
            rawAddress = value.address
        }

    private var rawOwnerWorkchain by Collections.ownerWorkchain
    private var rawOwnerAddress by Collections.ownerAddress

    var owner: MsgAddressInt.AddrStd
        get() = MsgAddressInt.AddrStd(rawOwnerWorkchain, rawOwnerAddress)
        set(value) {
            rawOwnerWorkchain = value.workchainId
            rawOwnerAddress = value.address
        }

    var size: Long by Collections.size

    var royaltyNumerator: Int? by Collections.royaltyNumerator
    var royaltyDenominator: Int? by Collections.royaltyDenominator
    val royalty: Float? by lazy {
        royaltyNumerator?.let { numerator -> royaltyDenominator?.let { denominator -> numerator.toFloat() / denominator } }
    }

    private var rawRoyaltyDestinationWorkchain by Collections.royaltyDestinationWorkchain
    private var rawRoyaltyDestinationAddress by Collections.royaltyDestinationAddress

    var royaltyDestination: MsgAddressInt.AddrStd?
        get() =
            rawRoyaltyDestinationWorkchain?.let { workchain ->
                rawRoyaltyDestinationAddress?.let { address ->
                    MsgAddressInt.AddrStd(
                        workchain,
                        address
                    )
                }
            }
        set(value) {
            rawRoyaltyDestinationWorkchain = value?.workchainId
            rawRoyaltyDestinationAddress = value?.address
        }

    val items by Item optionalReferrersOn Items.collection
}
