package money.tegro.market.drive.model

import money.tegro.market.db.ItemInfo
import money.tegro.market.drive.RoyaltyModel
import money.tegro.market.drive.toGoodString
import org.ton.block.MsgAddressIntStd

data class ItemModel(
    val address: String,
    val index: Long?,
    val collection: String?,
    val owner: String?,
    val royalty: RoyaltyModel?,
    val name: String?,
    val description: String?,
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
        image = ImageModel("TODO", "TODO", it.metadata?.image),
    )
}
