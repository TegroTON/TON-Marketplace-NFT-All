package money.tegro.market.service.collection

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import money.tegro.market.repository.ApprovalRepository
import org.springframework.stereotype.Service

@Service
class CollectionListService(
    private val approvalRepository: ApprovalRepository,
    private val collectionContractService: CollectionContractService,
) {
    fun get() =
        approvalRepository.findAllByApprovedIsTrue()
            .map { it.address }
            .filter { collectionContractService.get(it) != null }
}
