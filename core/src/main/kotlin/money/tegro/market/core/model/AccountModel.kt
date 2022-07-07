package money.tegro.market.core.model

import io.micronaut.data.annotation.EmbeddedId
import io.micronaut.data.annotation.MappedEntity
import io.swagger.v3.oas.annotations.media.Schema
import money.tegro.market.core.key.AddressKey
import org.ton.block.AddrStd
import java.time.Instant

@MappedEntity("accounts")
@Schema(hidden = true)
data class AccountModel(
    @EmbeddedId
    val address: AddressKey,

    val discovered: Instant = Instant.now(),
    val updated: Instant = Instant.now(),
) {
    companion object {
        @JvmStatic
        fun of(address: AddrStd): AccountModel = AccountModel(AddressKey.of(address))
    }
}
