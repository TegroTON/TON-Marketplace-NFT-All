package money.tegro.market.service.item

import money.tegro.market.service.SaleService
import mu.KLogging
import org.springframework.stereotype.Service
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt

@Service
class ItemOwnerService(
    private val itemContractService: ItemContractService,
    private val saleService: SaleService,
) {
    suspend fun get(address: MsgAddressInt): MsgAddress? {
        val owner = itemContractService.get(address)?.owner
        return ((owner as? MsgAddressInt)?.let { saleService.get(it)?.owner }
            ?: owner)
    }

    companion object : KLogging()
}
