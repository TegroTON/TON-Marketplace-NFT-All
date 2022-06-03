package money.tegro.market.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.ton.block.MsgAddressInt

class ItemEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ItemEntity>(ItemsTable) {
        @JvmStatic
        fun find(item: MsgAddressInt.AddrStd) =
            this.find { (ItemsTable.workchain eq item.workchainId) and (ItemsTable.address eq item.address) }
    }

    private var rawWorkchain by ItemsTable.workchain
    private var rawAddress by ItemsTable.address

    var address: MsgAddressInt.AddrStd
        get() = MsgAddressInt.AddrStd(rawWorkchain, rawAddress)
        set(value) {
            rawWorkchain = value.workchainId
            rawAddress = value.address
        }

    var initialized: Boolean by ItemsTable.initialized

    var index: Long? by ItemsTable.index
    var collection: CollectionEntity? by CollectionEntity optionalReferencedOn ItemsTable.collection

    private var rawOwnerWorkchain by ItemsTable.ownerWorkchain
    private var rawOwnerAddress by ItemsTable.ownerAddress

    var owner: MsgAddressInt.AddrStd?
        get() = rawOwnerWorkchain?.let { workchain ->
            rawOwnerAddress?.let { address ->
                MsgAddressInt.AddrStd(
                    workchain,
                    address
                )
            }
        }
        set(value) {
            rawOwnerWorkchain = value?.workchainId
            rawOwnerAddress = value?.address
        }

    var royaltyNumerator: Int? by ItemsTable.royaltyNumerator
    var royaltyDenominator: Int? by ItemsTable.royaltyDenominator
    val royalty: Float? by lazy {
        royaltyNumerator?.let { numerator -> royaltyDenominator?.let { denominator -> numerator.toFloat() / denominator } }
    }

    private var rawRoyaltyDestinationWorkchain by ItemsTable.royaltyDestinationWorkchain
    private var rawRoyaltyDestinationAddress by ItemsTable.royaltyDestinationAddress

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

    var metadataUrl by ItemsTable.metadataUrl
    var metadataIpfs by ItemsTable.metadataIpfs

    var name by ItemsTable.name
    var description by ItemsTable.description

    var imageUrl by ItemsTable.imageUrl
    var imageIpfs by ItemsTable.imageIpfs
    var imageData by ItemsTable.imageData

    val approved by ItemsTable.approved
    val discovered by ItemsTable.discovered
    var lastIndexed by ItemsTable.lastIndexed
}
