package money.tegro.market.drive.model

import io.swagger.v3.oas.annotations.media.Schema
import org.ton.block.MsgAddressIntStd

data class ImageModel(
    @Schema(description = "URL of the sized-down preview of the image")
    val preview: String,

    @Schema(description = "URL of the full-size image")
    val full: String,

    @Schema(description = "Original image url, extracted from the metadata")
    val original: String? = null,
) {
    constructor(
        name: String,
        address: MsgAddressIntStd,
        original: String?
    ) : this(
        "/api/v1/content/preview/${address.toGoodString()}/$name",
        "/api/v1/content/${address.toGoodString()}/$name",
        original,
    )
}
