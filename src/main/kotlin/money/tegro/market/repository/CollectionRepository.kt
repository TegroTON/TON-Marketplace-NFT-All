package money.tegro.market.repository

import money.tegro.market.model.CollectionModel
import org.springframework.data.repository.PagingAndSortingRepository
import org.ton.block.MsgAddressInt

interface CollectionRepository : PagingAndSortingRepository<CollectionModel, MsgAddressInt>
