package money.tegro.market.server.repository

import io.github.reactivecircus.cache4k.Cache
import money.tegro.market.contract.nft.RoyaltyContract
import money.tegro.market.toRaw
import mu.KLogging
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient
import java.util.*

class RoyaltyRepository(override val di: DI) : DIAware {
    private val liteClient: LiteClient by instance()

    private val approvalRepository: ApprovalRepository by instance()

    private val cache: Cache<MsgAddressInt, Optional<RoyaltyContract>> by instance()

    suspend fun get(royalty: MsgAddressInt): RoyaltyContract? =
        cache.get(royalty) {
            if (approvalRepository.isDisapproved(royalty)) { // Explicitly forbiddenden
                logger.debug { "address=${royalty.toRaw()} was disapproved" }
                Optional.empty()
            } else {
                logger.debug { "fetching royalty address=${royalty.toRaw()}" }
                RoyaltyContract.of(royalty as AddrStd, liteClient, liteClient.getLastBlockId())
                    .let { Optional.ofNullable(it) }
            }
        }
            .orElse(null)

    companion object : KLogging()
}
