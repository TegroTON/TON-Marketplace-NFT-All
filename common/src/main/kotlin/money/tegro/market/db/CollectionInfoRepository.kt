package money.tegro.market.db

import org.springframework.data.jpa.repository.JpaRepository

interface CollectionInfoRepository : JpaRepository<CollectionInfo, Long>, AddressableRepository<CollectionInfo>
