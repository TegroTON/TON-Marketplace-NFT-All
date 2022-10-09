package money.tegro.market.operations

import money.tegro.market.dto.BasicItemDTO

interface ItemOperations {
    suspend fun getBasicInfoByAddress(address: String): BasicItemDTO
}
