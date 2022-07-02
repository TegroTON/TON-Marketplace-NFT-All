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

@Controller("/content")
class ContentController(
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
) : ContentOperations {
    override fun getCollectionImage(collection: String): Mono<StreamedFile> =
        collectionRepository
            .findById(AddrStd(collection))
            .flatMap {
                mono {
                    it.imageData?.let { StreamedFile(ByteArrayInputStream(it), MediaType.ALL_TYPE) }
                        ?: it.image?.let { StreamedFile(URL(it)) }
                }
            }

    override fun getCollectionCoverImage(collection: String): Mono<StreamedFile> =
        collectionRepository
            .findById(AddrStd(collection))
            .flatMap {
                mono {
                    it.coverImageData?.let { StreamedFile(ByteArrayInputStream(it), MediaType.ALL_TYPE) }
                        ?: it.coverImage?.let { StreamedFile(URL(it)) }
                }
            }

    override fun getItemImage(item: String): Mono<StreamedFile> =
        itemRepository
            .findById(AddrStd(item))
            .flatMap {
                mono {
                    it.imageData?.let { StreamedFile(ByteArrayInputStream(it), MediaType.ALL_TYPE) }
                        ?: it.image?.let { StreamedFile(URL(it)) }
                }
            }

}
