package money.tegro.market.blockchain.nft

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
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

abstract class NFTCollection : Addressable {
    abstract val nextItemIndex: Long
    abstract val content: Cell
    abstract val owner: MsgAddress

    suspend fun itemAddresses(
        liteApi: LiteApi,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last },
    ): Flow<MsgAddress> =
        (0 until nextItemIndex)
            .asFlow()
            .map {
                this.itemAddress(it, liteApi, referenceBlock)
            }

    suspend fun items(
        liteApi: LiteApi,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last },
    ): Flow<NFTItem> =
        this.itemAddresses(liteApi, referenceBlock).map { NFTItem.of(it as AddrStd, liteApi, referenceBlock) }

    suspend fun itemAddress(
        index: Long, liteApi: LiteApi,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last },
    ): MsgAddress = itemAddressOf(address as AddrStd, index, liteApi, referenceBlock)

    suspend fun item(
        index: Long, liteApi: LiteApi,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last },
    ): NFTItem = NFTItem.of(this.itemAddress(index, liteApi, referenceBlock) as AddrStd, liteApi, referenceBlock)

    suspend fun royalty(
        liteApi: LiteApi,
        referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last }
    ) = NFTRoyalty.of(address as AddrStd, liteApi, referenceBlock)

    suspend fun metadata(
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
    ) = NFTCollectionMetadata.of(address, content, httpClient)

    companion object : KLogging() {
        private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteApi: LiteApi,
            referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last },
        ): NFTCollection =
            withLoggingContext(
                "address" to address.toString(userFriendly = true, bounceable = true)
            ) {
                liteApi.runSmcMethod(0b100, referenceBlock(), LiteServerAccountId(address), "get_collection_data")
                    .let {
                        if (it.exitCode != 0) {
                            logger.warn { "failed to run method" }
                            throw NFTException("failed to run method, exit code is ${it.exitCode}")
                        }

                        NFTCollectionImpl(
                            address,
                            (it[0] as VmStackValue.TinyInt).value,
                            (it[1] as VmStackValue.Cell).cell,
                            (it[2] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec)
                        )
                    }
            }

        @JvmStatic
        suspend fun itemAddressOf(
            collection: AddrStd,
            index: Long,
            liteApi: LiteApi,
            referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last },
        ): MsgAddress =
            withLoggingContext(
                "collection" to collection.toString(userFriendly = true, bounceable = true),
                "index" to index.toString(),
            ) {
                liteApi.runSmcMethod(
                    0b100,
                    referenceBlock(),
                    LiteServerAccountId(collection),
                    "get_nft_address_by_index",
                    VmStackValue.TinyInt(index)
                ).let {
                    if (it.exitCode != 0) {
                        logger.warn { "failed to run method" }
                        throw NFTException("failed to run method, exit code is ${it.exitCode}")
                    }

                    (it.first() as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec).apply {
                        withLoggingContext("item" to this.toString()) {
                            logger.trace { "item address successfully fetched" }
                        }
                    }
                }
            }

        @JvmStatic
        suspend fun itemContent(
            collection: AddrStd,
            index: Long,
            individualContent: Cell,
            liteApi: LiteApi,
            referenceBlock: suspend () -> TonNodeBlockIdExt = { liteApi.getMasterchainInfo().last },
        ): Cell =
            withLoggingContext(
                "collection" to collection.toString(userFriendly = true, bounceable = true),
                "index" to index.toString(),
                "individualContent" to individualContent.toString()
            ) {
                liteApi.runSmcMethod(
                    0b100,
                    referenceBlock(),
                    LiteServerAccountId(collection),
                    "get_nft_content",
                    VmStackValue.TinyInt(index),
                    VmStackValue.Cell(individualContent)
                ).let {
                    withLoggingContext("result" to it.toString()) {
                        if (it.exitCode != 0) {
                            logger.warn { "failed to run method" }
                            throw NFTException("failed to run method, exit code is ${it.exitCode}")
                        }

                        (it.first() as VmStackValue.Cell).cell.apply {
                            withLoggingContext("content" to this.toString()) {
                                logger.trace { "item content successfully fetched" }
                            }
                        }
                    }
                }
            }
    }
}

private data class NFTCollectionImpl(
    override val address: MsgAddress,
    override val nextItemIndex: Long,
    override val content: Cell,
    override val owner: MsgAddress
) : NFTCollection() {
}
