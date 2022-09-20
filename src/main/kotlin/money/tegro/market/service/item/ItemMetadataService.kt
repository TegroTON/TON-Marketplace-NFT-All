package money.tegro.market.service.item

import com.sksamuel.aedile.core.caffeineBuilder
import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.repository.ApprovalRepository
import money.tegro.market.service.ReferenceBlockService
import money.tegro.market.toRaw
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.stereotype.Service
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient
import java.util.*

@Service
class ItemMetadataService(
    private val liteClient: LiteClient,
    private val referenceBlockService: ReferenceBlockService,
    private val approvalRepository: ApprovalRepository,
    private val itemContractService: ItemContractService,
) {
    private val cache =
        caffeineBuilder<MsgAddressInt, Optional<ItemMetadata>> {
            // TODO: Configuration
        }.build()

    suspend fun get(address: MsgAddressInt): ItemMetadata? =
        cache.getOrPut(address) { item ->
            if (approvalRepository.existsByApprovedIsFalseAndAddress(item)) { // Explicitly forbidden
                logger.debug("{} was disapproved", kv("address", item.toRaw()))
                Optional.empty()
            } else {
                itemContractService.get(item)
                    ?.let { contract ->
                        ItemMetadata.of(
                            (contract.collection as? AddrStd) // Collection items
                                ?.let {
                                    CollectionContract.itemContent(
                                        it,
                                        contract.index,
                                        contract.individual_content,
                                        liteClient,
                                        referenceBlockService.get(),
                                    )
                                }
                                ?: contract.individual_content) // Standalone items
                    }
                    .let { Optional.ofNullable(it) }
            }
        }
            .orElse(null)

    companion object : KLogging()
}
