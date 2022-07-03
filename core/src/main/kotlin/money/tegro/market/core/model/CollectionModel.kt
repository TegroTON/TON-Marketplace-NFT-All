package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.micronaut.data.annotation.Relation
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.blockchain.nft.NFTCollection
import money.tegro.market.blockchain.nft.NFTCollectionMetadata
import money.tegro.market.core.dto.toKey
import money.tegro.market.core.key.AddressKey
import org.ton.block.AddrStd
import java.time.Instant

@MappedEntity("COLLECTIONS")
@Schema(hidden = true)
data class CollectionModel(
    @EmbeddedId
    val address: AddressKey,

    // Basic info
    val nextItemIndex: Long,

    @Relation(Relation.Kind.EMBEDDED)
    val owner: AddressKey,

    // Metadata information
    val name: String = "",

    val description: String = "",

    val image: String? = null,

    val imageData: ByteArray? = null,

    val coverImage: String? = null,

    val coverImageData: ByteArray? = null,


    val discovered: Instant = Instant.now(),
    val updated: Instant = Instant.now(),
) {
    fun copy(collection: NFTCollection): CollectionModel? {
        require((collection.address as? AddrStd) == this.address.to())
        return collection.owner.toKey()?.let { owner ->
            copy(
                nextItemIndex = collection.nextItemIndex,
                owner = owner,
                updated = Instant.now()
            )
        }
    }

    fun copy(metadata: NFTCollectionMetadata): CollectionModel {
        require((metadata.address as? AddrStd) == this.address.to())
        return copy(
            name = metadata.name.orEmpty(),
            description = metadata.description.orEmpty(),
            image = metadata.image,
            imageData = metadata.imageData,
            coverImage = metadata.coverImage,
            coverImageData = metadata.coverImageData,
            updated = Instant.now()
        )
    }

    companion object {
        @JvmStatic
        fun of(collection: NFTCollection, metadata: NFTCollectionMetadata): CollectionModel? =
            collection.address.toKey()?.let { address ->
                collection.owner.toKey()?.let { owner ->
                    CollectionModel(
                        address = address,
                        nextItemIndex = collection.nextItemIndex,
                        owner = owner,
                        name = metadata.name.orEmpty(),
                        description = metadata.description.orEmpty(),
                        image = metadata.image,
                        imageData = metadata.imageData,
                        coverImage = metadata.coverImage,
                        coverImageData = metadata.coverImageData,
                    )
                }
            }
    }
}

