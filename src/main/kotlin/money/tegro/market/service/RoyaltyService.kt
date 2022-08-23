package money.tegro.market.service

import money.tegro.market.contract.RoyaltyContract
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
class RoyaltyService(
    private val liteClient: LiteClient,
    private val approvalRepository: ApprovalRepository,
) {
    suspend fun get(address: MsgAddressInt): RoyaltyContract? =
        if (approvalRepository.existsByApprovedIsFalseAndAddress(address)) {
            ItemService.logger.debug("{} was disapproved", kv("address", address.toRaw()))
            null
        } else {
            try {
                logger.debug("updating royalty {}", kv("address", address.toRaw()))
                RoyaltyContract.of(address as AddrStd, liteClient)
            } catch (e: TvmException) {
                logger.warn("could not get royalty for {}, removing entry", kv("address", address.toRaw()), e)
                null
            }
        }

    companion object : KLogging()
}
