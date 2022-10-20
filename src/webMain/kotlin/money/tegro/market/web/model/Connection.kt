package money.tegro.market.web.model

import kotlinx.serialization.Serializable
import money.tegro.market.model.TransactionRequestModel

@Serializable
sealed interface Connection {
    val walletAddress: String
    val publicKey: String

    suspend fun isConnected(): Boolean

    suspend fun requestTransaction(request: TransactionRequestModel): Boolean
}
