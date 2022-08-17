package money.tegro.market.dto

import money.tegro.market.contract.ItemContract
import money.tegro.market.contract.RoyaltyContract
import money.tegro.market.core.toRaw
import money.tegro.market.metadata.ItemMetadata
import org.ton.block.MsgAddressInt

data class ItemDTO(
    val address: String,

    val initialized: Boolean,

    val index: ULong,

    val collection: String?,

    val owner: String?,

    val name: String?,

    val description: String?,

    val royalty: Float?,

    val royaltyDestination: String?
) {
    constructor(
        address: MsgAddressInt,
        contract: ItemContract,
        metadata: ItemMetadata,
        royalty: RoyaltyContract?,
    ) : this(
        address = address.toRaw(),
        initialized = contract.initialized,
        index = contract.index,
        collection = contract.collection.toRaw(),
        owner = contract.owner.toRaw(),
        name = metadata.name,
        description = metadata.description,
        royalty = royalty?.value(),
        royaltyDestination = royalty?.destination?.toRaw()
    )
}
