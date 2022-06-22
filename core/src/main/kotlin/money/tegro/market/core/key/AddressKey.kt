package money.tegro.market.core.key

import io.micronaut.data.annotation.Embeddable
import io.micronaut.data.annotation.MappedProperty
import org.ton.block.MsgAddressIntStd

@Embeddable
data class AddressKey(
    val workchain: Int,
    @MappedProperty(definition = "BINARY(32)")
    val address: ByteArray
) {
    fun to() = MsgAddressIntStd(workchain, address)

    companion object {
        @JvmStatic
        fun of(it: MsgAddressIntStd) = AddressKey(it.workchainId, it.address.toByteArray())
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
