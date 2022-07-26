package money.tegro.market.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(
    name = "WalletConfig",
    description = "Tonhub's ton-x -compatible representation of a wallet configuration, used for authentication"
)
data class WalletConfigDTO(
    @get:Schema(description = "Address of the wallet")
    val address: String,

    @get:Schema(description = "Wallet workchain id")
    val workchain: Int,

    @get:Schema(description = "Wallet ID")
    val walletId: Int,

    @get:Schema(description = "Wallet public key, encoded as base64")
    val publicKey: String,

    @get:Schema(description = "Session ID, base64")
    val session: String,

    @get:Schema(description = "Application endpoint")
    val endpoint: String,

    @get:Schema(description = "Application public key, encoded as base64")
    val appPublicKey: String,

    @get:Schema(description = "Wallet signature, base64")
    val walletSig: String,
)
