package money.tegro.market.service.item

import money.tegro.market.contract.nft.RoyaltyContract
import money.tegro.market.service.RoyaltyService
import mu.KLogging
import org.springframework.stereotype.Service
import org.ton.block.MsgAddressInt

@Service
class ItemRoyaltyService(
    private val itemContractService: ItemContractService,
    private val royaltyService: RoyaltyService,
) {
    suspend fun get(address: MsgAddressInt): RoyaltyContract? =
        (itemContractService.get(address)?.collection as? MsgAddressInt)
            ?.let { royaltyService.get(it) }
            ?: royaltyService.get(address)

    companion object : KLogging()
}
