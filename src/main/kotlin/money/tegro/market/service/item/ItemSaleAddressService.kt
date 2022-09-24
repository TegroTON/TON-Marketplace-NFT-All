package money.tegro.market.service.item

import money.tegro.market.service.SaleService
import mu.KLogging
import org.springframework.stereotype.Service
import org.ton.block.MsgAddressInt

@Service
class ItemSaleAddressService(
    private val itemContractService: ItemContractService,
    private val saleService: SaleService,
) {
    suspend fun get(address: MsgAddressInt): MsgAddressInt? {
        val owner = itemContractService.get(address)?.owner
        return if ((owner as? MsgAddressInt)?.let { saleService.get(it) } != null) {
            owner
        } else {
            null
        }
    }

    companion object : KLogging()
}
