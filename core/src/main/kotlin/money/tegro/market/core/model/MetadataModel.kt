package money.tegro.market.core.model

import java.time.Instant

interface MetadataModel : BasicModel {
    var content: ByteArray?

    var name: String?
    var description: String?
    var image: String?
    var imageData: ByteArray?
    var coverImage: String?
    var coverImageData: ByteArray?

    var metadataUpdated: Instant
    var metadataModified: Instant
}
