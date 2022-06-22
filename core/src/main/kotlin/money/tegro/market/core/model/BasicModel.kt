package money.tegro.market.core.model

import money.tegro.market.core.key.AddressKey
import java.time.Instant

interface BasicModel {
    val address: AddressKey

    val discovered: Instant
    var updated: Instant
    var modified: Instant
}
