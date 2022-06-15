package money.tegro.market.drive.model

import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.db.ItemInfo
import org.ton.block.MsgAddressIntStd

data class ItemModel(
    @Schema(description = "NFT item address, uniquely identifies it. Always base64url, bounceable")
    val address: String,

    @Schema(description = "Index of the item in the collection, if in one")
    val index: Long?,

    @Schema(description = "NFT collection address, uniquely identifies it. Always base64url, bounceable")
    val collection: String?,

    @Schema(description = "Address of the item owner, uniquely identifies the account. Base64url, bounceable")
    val owner: String?,

    @Schema(description = "Item royalty parameters, may be inherited from the collection or set up by the item itself")
    val royalty: RoyaltyModel?,

    @Schema(description = "Item name")
    val name: String?,

    @Schema(description = "Description of the item")
    val description: String?,

    @Schema(description = "Item image information")
    val image: ImageModel?,
) {
    constructor(it: ItemInfo) : this(
        address = it.addressStd().toGoodString(),
        index = it.index,
        collection = it.collection?.addressStd()?.toGoodString(),
        owner = it.ownerWorkchain?.let { wc -> it.ownerAddress?.let { addr -> MsgAddressIntStd(wc, addr) } }
            ?.toGoodString(),
        royalty = (it.royalty ?: it.collection?.royalty)?.let { royalty -> RoyaltyModel.of(royalty) },
        name = it.metadata?.name,
        description = it.metadata?.description,
        image = ImageModel("image", it.addressStd(), it.metadata?.image),
    )
}
