package money.tegro.market.core.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "TransactionRequest", description = "Tonhub's ton-x -compatible representation of a transaction request")
data class TransactionRequestDTO(
    @get:Schema(description = "Destination address. Always base64url, bounceable")
    val to: String,

    @get:Schema(description = "Amount in nano-tons")
    val value: Long,

    @get:Schema(description = "State_init cell, serialized as base64")
    val stateInit: String?,

    @get:Schema(description = "Payload cell, serialized as base64")
    val payload: String?,
)
