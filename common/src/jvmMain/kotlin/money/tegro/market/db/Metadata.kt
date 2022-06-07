package money.tegro.market.db

import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.statements.api.ExposedBlob

sealed interface Metadata {
    var name: String?
    var description: String?
    var image: String?
    var imageData: ExposedBlob?

    var metadataLastIndexed: Instant?
}
