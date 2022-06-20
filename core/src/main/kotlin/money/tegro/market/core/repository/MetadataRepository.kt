package money.tegro.market.core.repository

import io.micronaut.data.annotation.Id
import java.time.Instant

interface MetadataRepository {
    fun update(
        @Id id: Long,
        name: String?,
        description: String?,
        image: String?,
        imageData: ByteArray?,
        coverImage: String?,
        coverImageData: ByteArray?,
        metadataModified: Instant? = Instant.now(),
        metadataUpdated: Instant? = Instant.now(),
    ): Unit
}
