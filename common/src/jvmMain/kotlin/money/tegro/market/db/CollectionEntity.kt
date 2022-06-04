package money.tegro.market.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.ton.block.MsgAddressIntStd

class CollectionEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<CollectionEntity>(CollectionsTable) {
        @JvmStatic
        fun find(collection: MsgAddressIntStd) =
            this.find { (CollectionsTable.workchain eq collection.workchainId) and (CollectionsTable.address eq collection.address.toByteArray()) }
    }

    private var rawWorkchain by CollectionsTable.workchain
    private var rawAddress by CollectionsTable.address

    var address: MsgAddressIntStd
        get() = MsgAddressIntStd(rawWorkchain, rawAddress)
        set(value) {
            rawWorkchain = value.workchainId
            rawAddress = value.address.toByteArray()
        }

    private var rawOwnerWorkchain by CollectionsTable.ownerWorkchain
    private var rawOwnerAddress by CollectionsTable.ownerAddress

    var owner: MsgAddressIntStd
        get() = MsgAddressIntStd(rawOwnerWorkchain, rawOwnerAddress)
        set(value) {
            rawOwnerWorkchain = value.workchainId
            rawOwnerAddress = value.address.toByteArray()
        }

    var nextItemIndex: Long by CollectionsTable.nextItemIndex

    var royaltyNumerator: Int? by CollectionsTable.royaltyNumerator
    var royaltyDenominator: Int? by CollectionsTable.royaltyDenominator
    val royalty: Float? by lazy {
        royaltyNumerator?.let { numerator -> royaltyDenominator?.let { denominator -> numerator.toFloat() / denominator } }
    }

    private var rawRoyaltyDestinationWorkchain by CollectionsTable.royaltyDestinationWorkchain
    private var rawRoyaltyDestinationAddress by CollectionsTable.royaltyDestinationAddress

    var royaltyDestination: MsgAddressIntStd?
        get() =
            rawRoyaltyDestinationWorkchain?.let { workchain ->
                rawRoyaltyDestinationAddress?.let { address ->
                    MsgAddressIntStd(
                        workchain,
                        address
                    )
                }
            }
        set(value) {
            rawRoyaltyDestinationWorkchain = value?.workchainId
            rawRoyaltyDestinationAddress = value?.address?.toByteArray()
        }

    var metadataUrl by CollectionsTable.metadataUrl
    var metadataIpfs by CollectionsTable.metadataIpfs

    var name by CollectionsTable.name
    var description by CollectionsTable.description

    var imageUrl by CollectionsTable.imageUrl
    var imageIpfs by CollectionsTable.imageIpfs
    var imageData by CollectionsTable.imageData

    var coverImageUrl by CollectionsTable.coverImageUrl
    var coverImageIpfs by CollectionsTable.coverImageIpfs
    var coverImageData by CollectionsTable.coverImageData

    val approved by CollectionsTable.approved
    var discovered by CollectionsTable.discovered
    var lastIndexed by CollectionsTable.lastIndexed

    val items by ItemEntity optionalReferrersOn ItemsTable.collection
}
