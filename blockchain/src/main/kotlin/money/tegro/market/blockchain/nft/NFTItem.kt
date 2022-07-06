package money.tegro.market.blockchain.nft

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import money.tegro.market.blockchain.referenceBlock
import mu.KLogging
import mu.withLoggingContext
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.VmStackValue
import org.ton.cell.Cell
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

abstract class NFTItem : Addressable {
    abstract val initialized: Boolean
    abstract val index: Long
    abstract val collection: MsgAddress
    abstract val owner: MsgAddress
    abstract val individualContent: Cell

    fun isInCollection() = collection is AddrStd

    suspend fun collection(
        liteApi: LiteApi,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last }
    ) = (collection as? AddrStd)?.let { NFTCollection.of(it, liteApi, referenceBlock) }

    suspend fun royalty(
        liteApi: LiteApi,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last }
    ) = (collection as? AddrStd)?.let { NFTRoyalty.of(it, liteApi, referenceBlock) }
        ?: NFTRoyalty.of(address as AddrStd, liteApi, referenceBlock)

    suspend fun sale(
        liteApi: LiteApi,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last }
    ) = (owner as? AddrStd)?.let { NFTSale.of(it, liteApi, referenceBlock) }

    suspend fun content(
        liteApi: LiteApi,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last },
    ): Cell = (collection as? AddrStd)?.let {
        NFTCollection.itemContent(
            it,
            index,
            individualContent,
            liteApi,
            referenceBlock
        )
    } ?: individualContent

    suspend fun metadata(
        liteApi: LiteApi,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last },
        httpClient: HttpClient = HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            }
            install(HttpRequestRetry) {
                exponentialDelay()
                retryIf(maxRetries = 100) { _, response ->
                    !response.status.isSuccess() ||
                            response.contentType() != ContentType.parse("application/json") ||
                            response.contentLength() == 0L
                }
            }
        }
    ) = NFTItemMetadata.of(address, content(liteApi, referenceBlock), httpClient)

    companion object : KLogging() {
        private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteApi: LiteApi,
            referenceBlock: suspend () -> TonNodeBlockIdExt = liteApi.referenceBlock(),
        ): NFTItem =
            withLoggingContext(
                "address" to address.toString(userFriendly = true, bounceable = true)
            ) {
                liteApi.runSmcMethod(0b100, referenceBlock(), LiteServerAccountId(address), "get_nft_data")
                    .let {
                        if (it.exitCode != 0) {
                            logger.warn { "failed to run method" }
                            throw NFTException("failed to run method, exit code is ${it.exitCode}")
                        }

                        NFTItemImpl(
                            address,
                            (it[0] as VmStackValue.TinyInt).value == -1L,
                            (it[1] as VmStackValue.TinyInt).value,
                            (it[2] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec),
                            (it[3] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec),
                            (it[4] as VmStackValue.Cell).cell,
                        )
                    }
            }
    }
}

private data class NFTItemImpl(
    override val address: MsgAddress,
    override val initialized: Boolean,
    override val index: Long,
    override val collection: MsgAddress,
    override val owner: MsgAddress,
    override val individualContent: Cell
) : NFTItem() {

}
