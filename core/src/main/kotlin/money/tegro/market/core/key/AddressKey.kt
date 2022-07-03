package money.tegro.market.core.key

import io.micronaut.data.annotation.Embeddable
import io.micronaut.data.annotation.MappedProperty
import org.ton.block.AddrStd
import org.ton.block.MsgAddress

@Embeddable
data class AddressKey(
    val workchain: Int,
    @MappedProperty(definition = "BINARY(32)")
    val address: ByteArray
) {
    fun to() = AddrStd(workchain, address)

    companion object {
        @JvmStatic
        fun of(it: AddrStd) = AddressKey(it.workchain_id, it.address.toByteArray())

        @JvmStatic
        fun of(it: MsgAddress) = (it as? AddrStd)?.let { of(it) }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddressKey

        if (workchain != other.workchain) return false
        if (!address.contentEquals(other.address)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = workchain
        result = 31 * result + address.contentHashCode()
        return result
    }
}
