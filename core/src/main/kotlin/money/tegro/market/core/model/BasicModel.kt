package money.tegro.market.core.model

import org.ton.block.MsgAddressIntStd
import java.time.Instant

interface BasicModel {
    val workchain: Int
    val address: ByteArray

    val discovered: Instant
    var dataUpdated: Instant?
    var dataModified: Instant?

    var id: Long?
}

fun BasicModel.addressStd() = MsgAddressIntStd(workchain, address)
