package money.tegro.market.dto

import money.tegro.market.contract.CollectionContract
import money.tegro.market.contract.RoyaltyContract
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.toRaw
import org.ton.block.MsgAddressInt

data class CollectionDTO(
    val address: String,

    val size: ULong,

    val owner: String?,

    val name: String?,

    val description: String?,

    val image: String?,

    val coverImage: String?,

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
        image = metadata.image,
        coverImage = metadata.coverImage,
        royalty = royalty?.value(),
        royaltyDestination = royalty?.destination?.toRaw()
    )
}
