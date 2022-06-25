package money.tegro.market.drive

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.server.types.files.StreamedFile
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.model.MetadataModel
import money.tegro.market.core.operations.ContentOperations
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import money.tegro.market.core.repository.findByAddressStd
import org.ton.block.AddrStd
import java.io.ByteArrayInputStream
import java.net.URL

@Controller("/content")
class ContentController(
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
) : ContentOperations {
    override fun getCollectionImage(collection: String) =
        collectionRepository
            .findByAddressStd(AddrStd(collection))
            .flatMap { mono { getImage(it) } }

    override fun getCollectionCoverImage(collection: String) =
        collectionRepository
            .findByAddressStd(AddrStd(collection))
            .flatMap { mono { getCoverImage(it) } }

    override fun getItemImage(item: String) =
        itemRepository
            .findByAddressStd(AddrStd(item))
            .flatMap { mono { getImage(it) } }

    private fun getImage(model: MetadataModel) =
        model.imageData?.let { StreamedFile(ByteArrayInputStream(it), MediaType.ALL_TYPE) }
            ?: model.image?.let { StreamedFile(URL(it)) }

    private fun getCoverImage(model: MetadataModel) =
        model.coverImageData?.let { StreamedFile(ByteArrayInputStream(it), MediaType.ALL_TYPE) }
            ?: model.coverImage?.let { StreamedFile(URL(it)) }
}
