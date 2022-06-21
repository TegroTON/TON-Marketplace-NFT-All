package money.tegro.market.core.model

import org.ton.block.MsgAddressIntStd
import java.time.Instant

interface BasicModel {
    val workchain: Int
    val address: ByteArray

    var ownerWorkchain: Int?
    var ownerAddress: ByteArray?

    val discovered: Instant
    var dataUpdated: Instant?
    var dataModified: Instant?

    var id: Long?
}

fun BasicModel.addressStd() = MsgAddressIntStd(workchain, address)
fun BasicModel.ownerStd() = ownerWorkchain?.let { wc -> ownerAddress?.let { addr -> MsgAddressIntStd(wc, addr) } }
