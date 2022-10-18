package money.tegro.market.repository

import kotlinx.coroutines.flow.Flow
import money.tegro.market.model.ApprovalModel
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.ton.block.MsgAddressInt

interface ApprovalRepository : CoroutineSortingRepository<ApprovalModel, MsgAddressInt> {
    suspend fun existsByApprovedIsTrueAndAddress(addressInt: MsgAddressInt): Boolean
    suspend fun existsByApprovedIsFalseAndAddress(addressInt: MsgAddressInt): Boolean

    fun findAllByApprovedIsTrue(): Flow<ApprovalModel>
}
