package money.tegro.market.repository

import money.tegro.market.core.model.RoyaltyModel
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.ton.block.MsgAddressInt

interface RoyaltyRepository : CoroutineSortingRepository<RoyaltyModel, MsgAddressInt>
