package money.tegro.market.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import money.tegro.market.contract.nft.CollectionContract
import money.tegro.market.metadata.CollectionMetadata
import money.tegro.market.repository.ApprovalRepository
import money.tegro.market.toRaw
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.stereotype.Service
import org.ton.api.exception.TvmException
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrNone
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.lite.client.LiteClient

@Service
class CollectionService(
    private val liteClient: LiteClient,
    private val approvalRepository: ApprovalRepository,
) {
    fun listAll() =
        approvalRepository.findAllByApprovedIsTrue()
            .map { it.address }
            .filter { getContract(it) != null }

    suspend fun getContract(address: MsgAddressInt, referenceBlock: TonNodeBlockIdExt? = null): CollectionContract? =
        if (approvalRepository.existsByApprovedIsTrueAndAddress(address)) {
            try {
                logger.debug("fetching collection {}", kv("address", address.toRaw()))
                CollectionContract.of(address as AddrStd, liteClient)
            } catch (e: TvmException) {
                logger.warn("could not get collection information for {}", kv("address", address.toRaw()), e)
                null
            }
        } else {
            logger.warn("{} was not approved", kv("address", address.toRaw()))
            null
        }

    suspend fun getMetadata(address: MsgAddressInt): CollectionMetadata? =
        getContract(address)?.let { CollectionMetadata.of(it.content) }

    suspend fun getItemAddress(
        address: MsgAddressInt,
        index: ULong,
        referenceBlock: TonNodeBlockIdExt? = null
    ): MsgAddress =
        if (approvalRepository.existsByApprovedIsTrueAndAddress(address)) {
            try {
                CollectionContract.itemAddressOf(address as AddrStd, index, liteClient, referenceBlock)
            } catch (e: TvmException) {
                logger.warn(
                    "could not item {} address of {}",
                    kv("index", index.toString()),
                    kv("collection", address.toRaw())
                )
                AddrNone
            }
        } else {
            logger.warn("{} was not approved", kv("address", address.toRaw()))
            AddrNone
        }

    suspend fun listItemAddresses(
        address: MsgAddressInt,
        referenceBlock: TonNodeBlockIdExt? = null
    ): Flow<MsgAddress> {
        val refBlock = referenceBlock
            ?: liteClient.getLastBlockId() // To make sure all consequent calls are against the same block

        return (0uL until (getContract(address, refBlock)?.next_item_index ?: 0uL))
            .asFlow()
            .map { getItemAddress(address, it, refBlock) }
    }

    companion object : KLogging()
}
