package money.tegro.market.db

import org.ton.block.MsgAddressIntStd

abstract class AddressableEntity {
    abstract val workchain: Int
    abstract val address: ByteArray

    fun addressStd() = MsgAddressIntStd(workchain, address)
}
