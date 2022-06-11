package money.tegro.market.db

import org.springframework.data.jpa.repository.JpaRepository

interface ItemInfoRepository : JpaRepository<ItemInfo, Long>, AddressableRepository<ItemInfo>
