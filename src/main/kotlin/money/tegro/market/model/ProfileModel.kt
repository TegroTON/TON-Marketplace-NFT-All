package money.tegro.market.model

import mu.KLogging
import org.ton.block.MsgAddressInt

data class ProfileModel(
    val address: MsgAddressInt,
) {
    companion object : KLogging() {
        @JvmStatic
        fun of(
            address: MsgAddressInt,
        ): ProfileModel = ProfileModel(address)
    }
}
