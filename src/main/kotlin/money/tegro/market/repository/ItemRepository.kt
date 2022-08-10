package money.tegro.market.repository

import money.tegro.market.model.ItemModel
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.ton.block.MsgAddressInt

interface ItemRepository : CoroutineSortingRepository<ItemModel, MsgAddressInt>
