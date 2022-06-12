package money.tegro.market.drive.model

import money.tegro.market.drive.toGoodString
import org.ton.block.MsgAddressIntStd

data class ImageModel(
    val preview: String,
    val full: String,
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
