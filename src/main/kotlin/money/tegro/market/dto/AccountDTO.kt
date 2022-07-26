package money.tegro.market.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "Account", description = "Information about an account that holds items/collections")
data class AccountDTO(
    @get:Schema(description = "Address of the account, uniquely identifies it. Always base64url, bounceable")
    val address: String,
)
