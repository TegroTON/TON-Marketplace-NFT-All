package money.tegro.market.nightcrawler

import money.tegro.market.db.CollectionInfo
import org.springframework.data.jpa.repository.JpaRepository
import org.ton.block.MsgAddressIntStd

interface CollectionInfoRepository : JpaRepository<CollectionInfo, Long> {
    fun findByWorkchainAndAddress(workchain: Int, address: ByteArray): CollectionInfo?
}

fun CollectionInfoRepository.findByAddress(address: MsgAddressIntStd) =
    this.findByWorkchainAndAddress(address.workchainId, address.address.toByteArray())
