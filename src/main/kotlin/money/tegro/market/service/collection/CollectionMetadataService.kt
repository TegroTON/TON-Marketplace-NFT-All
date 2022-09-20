package money.tegro.market.service.collection

import com.sksamuel.aedile.core.caffeineBuilder
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.repository.ApprovalRepository
import money.tegro.market.toRaw
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.stereotype.Service
import org.ton.block.MsgAddressInt
import java.util.*

@Service
class CollectionMetadataService(
    private val collectionContractService: CollectionContractService,
    private val approvalRepository: ApprovalRepository,
) {
    private val cache =
        caffeineBuilder<MsgAddressInt, Optional<CollectionMetadata>> {
            // TODO: Configuration
        }.build()

    suspend fun get(address: MsgAddressInt): CollectionMetadata? =
        cache.getOrPut(address) { collection ->
            if (approvalRepository.existsByApprovedIsTrueAndAddress(collection)) { // Has been explicitly approved
                collectionContractService.get(collection)
                    ?.let {
                        CollectionMetadata.of(it.content)
                    }
                    .let { Optional.ofNullable(it) }
            } else {
                logger.warn("{} was not approved", kv("address", collection.toRaw()))
                Optional.empty()
            }
        }
            .orElse(null)

    companion object : KLogging()
}
