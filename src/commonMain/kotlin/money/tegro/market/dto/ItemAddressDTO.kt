package money.tegro.market.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemAddressDTO(
    val index: ULong,
    val address: String
)
