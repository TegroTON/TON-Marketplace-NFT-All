package money.tegro.market.server.service

import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.contract.nft.ItemContract
import money.tegro.market.contract.nft.RoyaltyContract
import money.tegro.market.contract.nft.SaleContract
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.toRaw
import mu.KLogging
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import java.util.*

class EvictionService(override val di: DI) : DIAware {
    private val liveBlockService: LiveBlockService by instance()

    private val collectionContractCache: Cache<MsgAddressInt, Optional<CollectionContract>> by instance()
    private val collectionMetadataCache: Cache<MsgAddressInt, Optional<CollectionMetadata>> by instance()

    private val itemContractCache: Cache<MsgAddressInt, Optional<ItemContract>> by instance()
    private val itemMetadataCache: Cache<MsgAddressInt, Optional<ItemMetadata>> by instance()

    private val saleCache: Cache<MsgAddressInt, Optional<SaleContract>> by instance()
    private val royaltyCache: Cache<MsgAddressInt, Optional<RoyaltyContract>> by instance()

    private val allCaches = listOf(
        collectionContractCache,
        collectionMetadataCache,
        itemContractCache,
        itemMetadataCache,
        saleCache,
        royaltyCache
    )

    @OptIn(FlowPreview::class)
    private val backgroundJob =
        CoroutineScope(Dispatchers.Default + CoroutineName("evictionService")).launch {
            liveBlockService.data
                .flatMapConcat { block ->
                    block.extra.account_blocks.toMap()
                        .keys
                        .map { AddrStd(block.info.shard.workchain_id, it.account_addr) }
                        .asFlow()
                }
                .collect { address ->
                    allCaches.filter { it.get(address) != null }
                        .onEach { logger.debug { "entity ${address.toRaw()} matched cache $it" } }
                        .forEach {
                            it.invalidate(address)
                        }
                }
        }

    companion object : KLogging()
}
