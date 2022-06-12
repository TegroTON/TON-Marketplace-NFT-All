package money.tegro.market.drive.model

import money.tegro.market.db.CollectionInfo
import money.tegro.market.drive.RoyaltyModel
import money.tegro.market.drive.toGoodString

data class CollectionModel(
    val address: String,
    val size: Long?,
    val owner: String?,
    val royalty: RoyaltyModel?,
    val name: String?,
    val description: String?,
    val image: ImageModel?,
    val coverImage: ImageModel?,
) {
    constructor(it: CollectionInfo) : this(
        address = it.addressStd().toGoodString(),
        size = it.nextItemIndex,
        owner = it.owner()?.toGoodString(),
        royalty = it.royalty?.let { royalty -> RoyaltyModel.of(royalty) },
        name = it.metadata?.name,
        description = it.metadata?.description,
        image = ImageModel("TODO", "TODO", it.metadata?.image),
        coverImage = ImageModel("TODO", "TODO", it.metadata?.coverImage),
    )
}
