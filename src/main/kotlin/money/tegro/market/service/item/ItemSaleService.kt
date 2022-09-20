package money.tegro.market.service.item

import money.tegro.market.service.SaleService
import mu.KLogging
import org.springframework.stereotype.Service
import org.ton.block.MsgAddressInt

@Service
class ItemSaleService(
    private val itemContractService: ItemContractService,
    private val saleService: SaleService,
) {
    suspend fun get(address: MsgAddressInt) =
        (itemContractService.get(address)?.owner as? MsgAddressInt)?.let { saleService.get(it) }

    companion object : KLogging()
}
