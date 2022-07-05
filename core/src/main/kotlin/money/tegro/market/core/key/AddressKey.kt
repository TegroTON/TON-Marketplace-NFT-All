package money.tegro.market.core.key

import io.micronaut.data.annotation.Embeddable
import org.ton.block.AddrStd
import org.ton.block.MsgAddress

@Embeddable
data class AddressKey(
    val workchain: Int,
    val hash: ByteArray
) {
    fun to() = AddrStd(workchain, hash)

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
        if (!hash.contentEquals(other.hash)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = workchain
        result = 31 * result + hash.contentHashCode()
        return result
    }
}
