package money.tegro.market.repository

import money.tegro.market.model.ItemModel
import org.springframework.data.repository.PagingAndSortingRepository
import org.ton.block.MsgAddressInt

interface ItemRepository : PagingAndSortingRepository<ItemModel, MsgAddressInt>
