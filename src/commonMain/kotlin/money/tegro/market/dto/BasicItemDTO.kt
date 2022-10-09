package money.tegro.market.dto

import kotlinx.serialization.Serializable

@Serializable
data class BasicItemDTO(
    val address: String,
    val name: String,
    val image: ImageDTO,
)
