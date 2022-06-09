package money.tegro.market.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.ton.block.MsgAddressIntStd

class ItemEntity(id: EntityID<Long>) : LongEntity(id), Royalty {
    companion object : LongEntityClass<ItemEntity>(ItemsTable) {
        @JvmStatic
        fun find(item: MsgAddressIntStd) =
            this.find { (ItemsTable.workchain eq item.workchainId) and (ItemsTable.address eq item.address.toByteArray()) }
    }

    var workchain by ItemsTable.workchain
    var address by ItemsTable.address

    var initialized: Boolean by ItemsTable.initialized
    var index: Long? by ItemsTable.index
    var collection: CollectionEntity? by CollectionEntity optionalReferencedOn ItemsTable.collection
    var ownerWorkchain by ItemsTable.ownerWorkchain
    var ownerAddress by ItemsTable.ownerAddress

    override var royaltyNumerator: Int? by ItemsTable.royaltyNumerator
    override var royaltyDenominator: Int? by ItemsTable.royaltyDenominator
    override var royaltyDestinationWorkchain by ItemsTable.royaltyDestinationWorkchain
    override var royaltyDestinationAddress by ItemsTable.royaltyDestinationAddress

    var marketplaceWorkchain by ItemsTable.marketplaceWorkchain
    var marketplaceAddress by ItemsTable.marketplaceAddress
    var sellerWorkchain by ItemsTable.sellerWorkchain
    var sellerAddress by ItemsTable.sellerAddress
    var price by ItemsTable.price
    var marketplaceFee by ItemsTable.marketplaceFee

    var saleRoyaltyDestinationWorkchain by ItemsTable.saleRoyaltyDestinationWorkchain
    var saleRoyaltyDestinationAddress by ItemsTable.saleRoyaltyDestinationAddress
    var saleRoyalty by ItemsTable.saleRoyalty

    var name by ItemsTable.name
    var description by ItemsTable.description
    var image by ItemsTable.image
    var imageData by ItemsTable.imageData

    val attributes by ItemAttributeEntity referrersOn ItemAttributesTable.item

    val approved by ItemsTable.approved
    var discovered by ItemsTable.discovered
    var dataLastIndexed by ItemsTable.dataLastIndexed
    override var royaltyLastIndexed by ItemsTable.royaltyLastIndexed
    var ownerLastIndexed by ItemsTable.ownerLastIndexed
    var metadataLastIndexed by ItemsTable.metadataLastIndexed
}
