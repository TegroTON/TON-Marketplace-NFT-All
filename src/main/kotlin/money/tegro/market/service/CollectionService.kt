package money.tegro.market.service

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import money.tegro.market.contract.CollectionContract
import money.tegro.market.core.toRaw
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.repository.CollectionRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.stereotype.Service
import org.ton.api.exception.TvmException
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient

@Service
class CollectionService(
    private val liteClient: LiteClient,
    private val collectionRepository: CollectionRepository,
) {
    fun all() = collectionRepository.findAll().asFlow().filter { it.approved }.map { it.address }

    suspend fun getContract(address: MsgAddressInt): CollectionContract? =
        try {
            logger.debug("fetching collection {}", kv("address", address.toRaw()))
            CollectionContract.of(address as AddrStd, liteClient)
        } catch (e: TvmException) {
            logger.warn("could not get collection information for {}", kv("address", address.toRaw()), e)
            null
        }

    suspend fun getMetadata(address: MsgAddressInt): CollectionMetadata? =
        getContract(address)?.let { CollectionMetadata.of(it.content) }

    suspend fun getItemAddress(address: MsgAddressInt, index: ULong): MsgAddress =
        CollectionContract.itemAddressOf(address as AddrStd, index, liteClient)

    companion object : KLogging()
}
