package money.tegro.market.drive

import money.tegro.market.db.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.ton.block.MsgAddressIntStd

fun MsgAddressIntStd.toGoodString() = this.toString(userFriendly = true, urlSafe = true, bounceable = true)

data class RoyaltyModel(
    val value: Float,
    val destination: String?,
) {
    companion object {
        fun of(it: RoyaltyEntity) = it.numerator?.let { n ->
            it.denominator?.let { d ->
                it.destinationWorkchain?.let { wc ->
                    it.destinationAddress?.let { addr ->
                        RoyaltyModel(
                            n.toFloat() / d,
                            MsgAddressIntStd(wc, addr).toGoodString()
                        )
                    }
                }
            }
        }
    }
}

data class ImageModel(
    val preview: String,
    val full: String,
)

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
        image = ImageModel("TODO", "TODO"),
        coverImage = ImageModel("TODO", "TODO"),
    )
}

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
        image = ImageModel("TODO", "TODO"),
    )
}

@RestController
class APIv1(val collectionInfoRepository: CollectionInfoRepository, val itemInfoRepository: ItemInfoRepository) {
    @GetMapping("/{address}")
    fun getCollection(@PathVariable address: String) =
        collectionInfoRepository.findByAddress(MsgAddressIntStd(address))
            ?.let { CollectionModel(it) }

    @GetMapping("/{address}/items")
    @Transactional
    fun getCollectionItems(@PathVariable address: String, @RequestParam(defaultValue = "100") limit: Int) =
        collectionInfoRepository.findByAddress(MsgAddressIntStd(address))?.items
            .orEmpty()
            .filter { it.initialized }
            .sortedBy { it.index }
            .take(minOf(limit, 100))
            .map { ItemModel(it) }
}
