package money.tegro.market.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.ton.block.MsgAddressIntStd

class CollectionEntity(id: EntityID<Long>) : LongEntity(id), Royalty {
    companion object : LongEntityClass<CollectionEntity>(CollectionsTable) {
        @JvmStatic
        fun find(collection: MsgAddressIntStd) =
            this.find { (CollectionsTable.workchain eq collection.workchainId) and (CollectionsTable.address eq collection.address.toByteArray()) }
    }

    var workchain by CollectionsTable.workchain
    var address by CollectionsTable.address
    var ownerWorkchain by CollectionsTable.ownerWorkchain
    var ownerAddress by CollectionsTable.ownerAddress
    var nextItemIndex by CollectionsTable.nextItemIndex

    val items by ItemEntity optionalReferrersOn ItemsTable.collection

    override var royaltyNumerator: Int? by CollectionsTable.royaltyNumerator
    override var royaltyDenominator: Int? by CollectionsTable.royaltyDenominator
    override var royaltyDestinationWorkchain by CollectionsTable.royaltyDestinationWorkchain
    override var royaltyDestinationAddress by CollectionsTable.royaltyDestinationAddress

    var name by CollectionsTable.name
    var description by CollectionsTable.description
    var image by CollectionsTable.image
    var imageData by CollectionsTable.imageData
    var coverImage by CollectionsTable.coverImage
    var coverImageData by CollectionsTable.coverImageData

    val approved by CollectionsTable.approved
    var discovered by CollectionsTable.discovered
    var dataLastIndexed by CollectionsTable.dataLastIndexed
    override var royaltyLastIndexed by CollectionsTable.royaltyLastIndexed
    var metadataLastIndexed by CollectionsTable.metadataLastIndexed
}
