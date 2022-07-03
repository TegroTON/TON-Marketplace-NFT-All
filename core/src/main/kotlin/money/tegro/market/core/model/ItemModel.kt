package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.blockchain.nft.NFTItemMetadata
import money.tegro.market.core.dto.toKey
import money.tegro.market.core.key.AddressKey
import org.ton.block.AddrStd
import java.time.Instant

@MappedEntity("ITEMS")
@Schema(hidden = true)
data class ItemModel(
    @EmbeddedId
    val address: AddressKey,

    // Basic info
    val initialized: Boolean,

    val index: Long,

    @Relation(Relation.Kind.EMBEDDED)
    val collection: AddressKey?, // Items may be stand-alone and not bellong to any collection

    @Relation(Relation.Kind.EMBEDDED)
    val owner: AddressKey,


    // Metadata information
    val name: String = "",

    val description: String = "",

    val image: String? = null,

    val imageData: ByteArray? = null,


    val discovered: Instant = Instant.now(),
    val updated: Instant = Instant.now(),
) {
    fun copy(item: NFTItem): ItemModel? {
        require((item.address as? AddrStd) == this.address.to())
        return item.owner.toKey()?.let { owner ->
            copy(
                initialized = item.initialized,
                index = item.index,
                collection = item.collection.toKey(), // It's okay if it's null
                owner = owner,
                updated = Instant.now()
            )
        }
    }

    fun copy(metadata: NFTItemMetadata): ItemModel {
        require((metadata.address as? AddrStd) == this.address.to())
        return copy(
            name = metadata.name.orEmpty(),
            description = metadata.description.orEmpty(),
            image = metadata.image,
            imageData = metadata.imageData,
            updated = Instant.now()
        )
    }

    companion object {
        @JvmStatic
        fun of(item: NFTItem, metadata: NFTItemMetadata): ItemModel? =
            item.address.toKey()?.let { address ->
                item.owner.toKey()?.let { owner ->
                    ItemModel(
                        address = address,
                        initialized = item.initialized,
                        index = item.index,
                        collection = item.collection.toKey(), // It's okay if it's null
                        owner = owner,
                        name = metadata.name.orEmpty(),
                        description = metadata.description.orEmpty(),
                        image = metadata.image,
                        imageData = metadata.imageData,
                    )
                }
            }
    }
}
