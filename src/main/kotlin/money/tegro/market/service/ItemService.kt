package money.tegro.market.service

import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.contract.nft.ItemContract
import money.tegro.market.metadata.ItemMetadata
import money.tegro.market.repository.ApprovalRepository
import money.tegro.market.toRaw
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.stereotype.Service
import org.ton.api.exception.TvmException
import org.ton.block.AddrStd
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient

@Service
class ItemService(
    private val liteClient: LiteClient,
    private val approvalRepository: ApprovalRepository,
) {
    suspend fun getContract(address: MsgAddressInt): ItemContract? =
        if (approvalRepository.existsByApprovedIsFalseAndAddress(address)) {
            logger.debug("{} was disapproved", kv("address", address.toRaw()))
            null
        } else {
            try {
                logger.debug("fetching item {}", kv("address", address.toRaw()))
                ItemContract.of(address as AddrStd, liteClient)
            } catch (e: TvmException) {
                logger.warn("could not get item information for {}", kv("address", address.toRaw()), e)
                null
            }
        }

    suspend fun getMetadata(address: MsgAddressInt): ItemMetadata? =
        getContract(address)?.let { item ->
            ItemMetadata.of((item.collection as? AddrStd)
                ?.let { CollectionContract.itemContent(it, item.index, item.individual_content, liteClient) }
                ?: item.individual_content)
        }

    companion object : KLogging()
}
