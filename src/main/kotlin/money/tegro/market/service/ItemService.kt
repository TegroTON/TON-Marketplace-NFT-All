package money.tegro.market.service

import money.tegro.market.contract.CollectionContract
import money.tegro.market.contract.ItemContract
import money.tegro.market.core.toRaw
import money.tegro.market.metadata.ItemMetadata
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
) {
    suspend fun getContract(address: MsgAddressInt): ItemContract? =
        try {
            logger.debug("fetching item {}", kv("address", address.toRaw()))
            ItemContract.of(address as AddrStd, liteClient)
        } catch (e: TvmException) {
            logger.warn("could not get item information for {}", kv("address", address.toRaw()), e)
            null
        }

    suspend fun getMetadata(address: MsgAddressInt): ItemMetadata? =
        getContract(address)?.let { item ->
            ItemMetadata.of((item.collection as? AddrStd)
                ?.let { CollectionContract.itemContent(it, item.index, item.individualContent, liteClient) }
                ?: item.individualContent)
        }

    companion object : KLogging()
}
