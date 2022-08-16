package money.tegro.market.dto

import money.tegro.market.contract.CollectionContract
import money.tegro.market.contract.RoyaltyContract
import money.tegro.market.core.toRaw
import money.tegro.market.metadata.CollectionMetadata
import org.ton.block.MsgAddressInt

data class CollectionDTO(
    val address: String,

    val size: Long,

    val owner: String?,

    val name: String?,

    val description: String?,

    val royalty: Float?,

    val royaltyDestination: String?
) {
    constructor(
        address: MsgAddressInt,
        contract: CollectionContract,
        metadata: CollectionMetadata,
        royalty: RoyaltyContract?,
    ) : this(
        address = address.toRaw(),
        size = contract.nextItemIndex,
        owner = contract.owner.toRaw(),
        name = metadata.name,
        description = metadata.description,
        royalty = royalty?.value(),
        royaltyDestination = royalty?.destination?.toRaw()
    )
}
