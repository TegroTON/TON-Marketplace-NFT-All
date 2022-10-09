package money.tegro.market.controller

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import money.tegro.market.dropTake
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.dto.ImageDTO
import money.tegro.market.dto.ItemAddressDTO
import money.tegro.market.dto.TopCollectionDTO
import money.tegro.market.operations.CollectionOperations
import money.tegro.market.repository.CollectionRepository
import money.tegro.market.toRaw
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.ton.block.MsgAddressInt

@RestController
class CollectionController(
    private val collectionRepository: CollectionRepository,
) : CollectionOperations {
    // TODO: actual top
    @RequestMapping("/api/v1/collections/top")
    override fun listTopCollections(): Flow<TopCollectionDTO> =
        collectionRepository.listAll()
            .mapNotNull { address ->
                collectionRepository.getMetadata(address)?.let { address to it }
            }
            .take(9)
            .map { (address, metadata) ->
                TopCollectionDTO(
                    address = address.toRaw(),
                    name = metadata.name ?: "Untitled Collection",
                    image = ImageDTO(
                        original = metadata.image
                    )
                )
            }

    @RequestMapping("/api/v1/collection/{address}")
    override suspend fun getByAddress(@PathVariable address: String): CollectionDTO {
        val collection = MsgAddressInt(address)
        val contract = requireNotNull(collectionRepository.getContract(collection))
        val metadata = collectionRepository.getMetadata(collection)

        return CollectionDTO(
            address = collection.toRaw(),
            numberOfItems = contract.next_item_index,
            owner = contract.owner.toRaw(),
            name = metadata?.name ?: "Untitled Collection",
            description = metadata?.description.orEmpty(),
            image = ImageDTO(
                original = metadata?.image
            ),
            coverImage = ImageDTO(
                original = metadata?.cover_image ?: metadata?.image
            ),
        )
    }

    @RequestMapping("/api/v1/collection/{address}/items")
    override fun listCollectionItems(
        @PathVariable address: String,
        @RequestParam(defaultValue = "null") drop: Int?,
        @RequestParam(defaultValue = "null") take: Int?
    ): Flow<ItemAddressDTO> =
        collectionRepository.listCollectionItems(MsgAddressInt(address))
            .mapNotNull { (index, addr) -> addr?.toRaw()?.let { ItemAddressDTO(index, it) } }
            .dropTake(drop, take)

}
