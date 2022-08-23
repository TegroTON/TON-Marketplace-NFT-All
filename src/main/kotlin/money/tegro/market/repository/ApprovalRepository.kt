package money.tegro.market.repository

import money.tegro.market.model.ApprovalModel
import org.springframework.data.repository.PagingAndSortingRepository
import org.ton.block.MsgAddressInt

interface ApprovalRepository : PagingAndSortingRepository<ApprovalModel, MsgAddressInt> {
    fun existsByApprovedIsTrueAndAddress(addressInt: MsgAddressInt): Boolean
    fun existsByApprovedIsFalseAndAddress(addressInt: MsgAddressInt): Boolean

    fun findAllByApprovedIsTrue(): List<ApprovalModel>
}
