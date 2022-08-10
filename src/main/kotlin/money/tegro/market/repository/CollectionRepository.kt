package money.tegro.market.repository

import money.tegro.market.model.CollectionModel
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.ton.block.MsgAddressInt

interface CollectionRepository : CoroutineSortingRepository<CollectionModel, MsgAddressInt>
