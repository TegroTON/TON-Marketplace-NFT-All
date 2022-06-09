package money.tegro.market.nightcrawler

import com.badoo.reaktive.coroutinesinterop.singleFromCoroutine
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.flatMapSingle
import com.badoo.reaktive.observable.observable
import io.ipfs.kotlin.IPFS
import money.tegro.market.nft.*
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.MsgAddressIntStd
import org.ton.lite.api.LiteApi

fun Observable<MsgAddressIntStd>.nftSaleOf(
    liteClient: LiteApi,
    referenceBlock: suspend () -> TonNodeBlockIdExt = { liteClient.getMasterchainInfo().last },
) =
    this.flatMapSingle { singleFromCoroutine { it to NFTSale.of(it, liteClient, referenceBlock()) } }

fun Observable<MsgAddressIntStd>.nftCollectionOf(
    liteClient: LiteApi,
    referenceBlock: suspend () -> TonNodeBlockIdExt = { liteClient.getMasterchainInfo().last },
) =
    this.flatMapSingle { singleFromCoroutine { NFTCollection.of(it, liteClient, referenceBlock()) } }

fun Observable<NFTCollection>.nftCollectionItems(
    liteClient: LiteApi,
    referenceBlock: suspend () -> TonNodeBlockIdExt = { liteClient.getMasterchainInfo().last },
) =
    this.flatMap {
        observable<Pair<MsgAddressIntStd, Long>> { emitter ->
            (0 until it.nextItemIndex).forEach { index ->
                emitter.onNext(it.address to index)
            }
            emitter.onComplete()
        }
    }
        .flatMapSingle(1) {
            singleFromCoroutine { NFTItem.of(it.first, it.second, liteClient, referenceBlock()) }
        }

fun Observable<MsgAddressIntStd>.nftItemOf(
    liteClient: LiteApi,
    referenceBlock: suspend () -> TonNodeBlockIdExt = { liteClient.getMasterchainInfo().last },
) =
    this.flatMapSingle { singleFromCoroutine { NFTItem.of(it, liteClient, referenceBlock()) } }


fun Observable<MsgAddressIntStd>.nftRoyaltyOf(
    liteClient: LiteApi,
    referenceBlock: suspend () -> TonNodeBlockIdExt = { liteClient.getMasterchainInfo().last },
) =
    this.flatMapSingle { singleFromCoroutine { it to NFTRoyalty.of(it, liteClient, referenceBlock()) } }

fun Observable<NFTCollection>.nftCollectionMetadata(ipfs: IPFS) = this.flatMapSingle {
    singleFromCoroutine { it.address to NFTMetadata.of<NFTCollectionMetadata>(it.content, ipfs) }
}

fun Observable<NFTItem>.nftItemMetadata(
    ipfs: IPFS, liteClient: LiteApi,
    referenceBlock: suspend () -> TonNodeBlockIdExt = { liteClient.getMasterchainInfo().last },
) =
    this.flatMapSingle {
        singleFromCoroutine {
            it.address to (it as? NFTItemInitialized)?.let {
                NFTMetadata.of<NFTItemMetadata>(
                    it.fullContent(
                        liteClient,
                        referenceBlock()
                    ), ipfs
                )
            }
        }
    }
