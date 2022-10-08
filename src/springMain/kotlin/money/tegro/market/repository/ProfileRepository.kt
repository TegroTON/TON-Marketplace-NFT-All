package money.tegro.market.repository

import money.tegro.market.model.ProfileModel
import mu.KLogging
import org.springframework.stereotype.Repository
import org.ton.block.MsgAddressInt

@Repository
class ProfileRepository() {
    suspend fun getByAddress(address: MsgAddressInt): ProfileModel =
        ProfileModel.of(
            address,
        )

    companion object : KLogging()
}
