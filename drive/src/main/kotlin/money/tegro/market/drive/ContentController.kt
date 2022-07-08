package money.tegro.market.drive

import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.server.types.files.StreamedFile
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.operations.ContentOperations
import money.tegro.market.core.repository.CollectionRepository
import money.tegro.market.core.repository.ItemRepository
import org.ton.block.AddrStd
import reactor.core.publisher.Mono
import java.io.ByteArrayInputStream
import java.net.URL
import javax.annotation.security.PermitAll

@PermitAll
@Controller("/content")
class ContentController(
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
) : ContentOperations {
    override fun getCollectionImage(collection: String): Mono<StreamedFile> =
        collectionRepository
            .findById(AddrStd(collection))
            .flatMap { mono { getFromDataOrUrl(it.imageData, it.image) } }

    override fun getCollectionCoverImage(collection: String): Mono<StreamedFile> =
        collectionRepository
            .findById(AddrStd(collection))
            .flatMap { mono { getFromDataOrUrl(it.coverImageData, it.coverImage) } }

    override fun getItemImage(item: String): Mono<StreamedFile> =
        itemRepository
            .findById(AddrStd(item))
            .flatMap { mono { getFromDataOrUrl(it.imageData, it.image) } }

    private fun getFromDataOrUrl(data: ByteArray, url: String?) =
        if (data.isNotEmpty()) {
            StreamedFile(ByteArrayInputStream(data), MediaType.ALL_TYPE)
        } else {
            url?.let { StreamedFile(URL(url)) }
        }
}
