package money.tegro.market.dto

import kotlinx.serialization.Serializable

@Serializable
data class TopCollectionDTO(
    val address: String,
    val name: String,
    val image: ImageDTO,
)
