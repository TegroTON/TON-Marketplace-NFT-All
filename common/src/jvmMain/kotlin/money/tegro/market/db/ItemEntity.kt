package money.tegro.market.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.ton.block.MsgAddressIntStd

class ItemEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ItemEntity>(ItemsTable) {
        @JvmStatic
        fun find(item: MsgAddressIntStd) =
            this.find { (ItemsTable.workchain eq item.workchainId) and (ItemsTable.address eq item.address.toByteArray()) }
    }

    var rawWorkchain by ItemsTable.workchain
    var rawAddress by ItemsTable.address

    var address: MsgAddressIntStd
        get() = MsgAddressIntStd(rawWorkchain, rawAddress)
        set(value) {
            rawWorkchain = value.workchainId
            rawAddress = value.address.toByteArray()
        }

    var initialized: Boolean by ItemsTable.initialized

    var index: Long? by ItemsTable.index
    var collection: CollectionEntity? by CollectionEntity optionalReferencedOn ItemsTable.collection

    var rawOwnerWorkchain by ItemsTable.ownerWorkchain
    var rawOwnerAddress by ItemsTable.ownerAddress

    var owner: MsgAddressIntStd?
        get() = rawOwnerWorkchain?.let { workchain ->
            rawOwnerAddress?.let { address ->
                MsgAddressIntStd(
                    workchain,
                    address
                )
            }
        }
        set(value) {
            rawOwnerWorkchain = value?.workchainId
            rawOwnerAddress = value?.address?.toByteArray()
        }

    var royaltyNumerator: Int? by ItemsTable.royaltyNumerator
    var royaltyDenominator: Int? by ItemsTable.royaltyDenominator
    val royalty: Float? by lazy {
        royaltyNumerator?.let { numerator -> royaltyDenominator?.let { denominator -> numerator.toFloat() / denominator } }
    }

    private var rawRoyaltyDestinationWorkchain by ItemsTable.royaltyDestinationWorkchain
    private var rawRoyaltyDestinationAddress by ItemsTable.royaltyDestinationAddress
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

    private var rawMarketplaceWorkchain by ItemsTable.marketplaceWorkchain
    private var rawMarketplaceAddress by ItemsTable.marketplaceAddress
    var marketplace: MsgAddressIntStd?
        get() =
            rawMarketplaceWorkchain?.let { workchain ->
                rawMarketplaceAddress?.let { address ->
                    MsgAddressIntStd(
                        workchain,
                        address
                    )
                }
            }
        set(value) {
            rawMarketplaceWorkchain = value?.workchainId
            rawMarketplaceAddress = value?.address?.toByteArray()
        }

    private var rawSellerWorkchain by ItemsTable.sellerWorkchain
    private var rawSellerAddress by ItemsTable.sellerAddress
    var seller: MsgAddressIntStd?
        get() =
            rawSellerWorkchain?.let { workchain ->
                rawSellerAddress?.let { address ->
                    MsgAddressIntStd(
                        workchain,
                        address
                    )
                }
            }
        set(value) {
            rawSellerWorkchain = value?.workchainId
            rawSellerAddress = value?.address?.toByteArray()
        }

    var price by ItemsTable.price
    var marketplaceFee by ItemsTable.marketplaceFee

    private var rawSaleRoyaltyDestinationWorkchain by ItemsTable.saleRoyaltyDestinationWorkchain
    private var rawSaleRoyaltyDestinationAddress by ItemsTable.saleRoyaltyDestinationAddress
    var saleRoyaltyDestination: MsgAddressIntStd?
        get() =
            rawSaleRoyaltyDestinationWorkchain?.let { workchain ->
                rawSaleRoyaltyDestinationAddress?.let { address ->
                    MsgAddressIntStd(
                        workchain,
                        address
                    )
                }
            }
        set(value) {
            rawSaleRoyaltyDestinationWorkchain = value?.workchainId
            rawSaleRoyaltyDestinationAddress = value?.address?.toByteArray()
        }

    var saleRoyalty by ItemsTable.saleRoyalty

    var metadataUrl by ItemsTable.metadataUrl
    var metadataIpfs by ItemsTable.metadataIpfs

    var name by ItemsTable.name
    var description by ItemsTable.description

    var imageUrl by ItemsTable.imageUrl
    var imageIpfs by ItemsTable.imageIpfs
    var imageData by ItemsTable.imageData

    val approved by ItemsTable.approved
    var discovered by ItemsTable.discovered
    var lastIndexed by ItemsTable.lastIndexed
}
