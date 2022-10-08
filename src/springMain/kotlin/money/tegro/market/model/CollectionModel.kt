package money.tegro.market.model

import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.metadata.CollectionMetadata
import mu.KLogging
import org.ton.block.AddrNone
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt

data class CollectionModel(
    val address: MsgAddressInt,
    val numberOfItems: Int,
    val owner: MsgAddress,
    val name: String,
    val description: String,
    val image: String,
    val coverImage: String,
) {
    companion object : KLogging() {
        @JvmStatic
        fun of(address: MsgAddressInt, contract: CollectionContract?, metadata: CollectionMetadata?) =
            CollectionModel(
                address = address,
                numberOfItems = contract?.next_item_index?.toInt() ?: 0,
                owner = contract?.owner ?: AddrNone,
                name = metadata?.name ?: "Untitled Collection",
                description = metadata?.description.orEmpty(),
                image = metadata?.image ?: "", // TODO
                coverImage = (metadata?.cover_image ?: metadata?.image) ?: "", // TODO
            )
    }
}
