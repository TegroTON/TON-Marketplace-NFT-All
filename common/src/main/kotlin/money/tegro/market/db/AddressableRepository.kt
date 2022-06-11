package money.tegro.market.db

import org.ton.block.MsgAddressIntStd

interface AddressableRepository<T : AddressableEntity> {
    fun findByWorkchainAndAddress(workchain: Int, address: ByteArray): T?
}

fun <T : AddressableEntity> AddressableRepository<T>.findByAddress(address: MsgAddressIntStd) =
    this.findByWorkchainAndAddress(address.workchainId, address.address.toByteArray())
