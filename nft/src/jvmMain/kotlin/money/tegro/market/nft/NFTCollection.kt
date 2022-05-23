package money.tegro.market.nft

import kotlinx.coroutines.*
import mu.KLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ton.block.MsgAddressInt
import org.ton.cell.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import kotlin.coroutines.CoroutineContext

sealed interface NFTCollection {
    abstract val address: MsgAddressInt
    abstract val size: Int
    abstract val content: NFTContent
    abstract val owner: MsgAddressInt

    suspend fun getItemAddress(index: Int) = NFTCollection.getItemAddress(this.address, index)

    suspend fun getItem(index: Int): NFTItem = NFTItem.fetch(getItemAddress(index))

    // Returns pair of percentage and destination for royalties
    suspend fun getRoyaltyParameters(): Pair<Float, MsgAddressInt>? =
        NFTCollection.getRoyaltyParameters(address)

    companion object : KoinComponent, KLogging() {
        @JvmStatic
        suspend fun fetch(address: MsgAddressInt): NFTCollection {
            val liteClient: LiteApi by inject()
            val lastBlock = liteClient.getMasterchainInfo().last
            logger.debug("last block: $lastBlock")

            val accountId = LiteServerAccountId((address as MsgAddressInt.AddrStd).workchain_id, address.address)

            logger.debug("running method `get_collection_data` on ${address.toString(userFriendly = true)}")
            val response = liteClient.runSmcMethod(
                0b100, // we only care about the result
                lastBlock,
                accountId,
                102491L, // get_collection_data
                BagOfCells(
                    CellBuilder.beginCell()
                        .storeUInt(0, 16)
                        .storeUInt(0, 8)
                        .endCell() // no parameters
                ).toByteArray()
            )
            logger.debug("response: $response")
            require(response.exitCode == 0) { "Failed to run the method, exit code is ${response.exitCode}" }

            var loader = BagOfCells(response.result!!).roots.first().beginParse()
            loader.loadUInt(16) // skip whatever this is
            loader.loadUInt(8) // number of entries

            loader.loadUInt(8)
            var next = loader.loadRef()
            var begin = loader.loadUInt(10).toInt()
            var end = loader.loadUInt(10).toInt()
            val owner = Cell(loader.loadRef().bits.slice(begin..end)).beginParse().loadMsgAddr()!!
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val contentCell = loader.loadRef()
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val nextItemIndex = loader.loadUInt(64).toInt()

            return NFTCollectionImpl(
                address,
                nextItemIndex,
                contentCell,
                owner
            )
        }

        @JvmStatic
        suspend fun getItemAddress(collection: MsgAddressInt, index: Int): MsgAddressInt {
            val liteClient: LiteApi by inject()

            val lastBlock = liteClient.getMasterchainInfo().last
            logger.debug("last block: $lastBlock")

            val accountId = LiteServerAccountId((collection as MsgAddressInt.AddrStd).workchain_id, collection.address)

            logger.debug("running method `get_nft_address_by_index` on ${collection.toString(userFriendly = true)}")
            val response = liteClient.runSmcMethod(
                0b00100, // we only care about the result
                lastBlock,
                accountId,
                92067L, // get_nft_address_by_index
                BagOfCells(
                    CellBuilder.beginCell()
                        .storeUInt(0, 16)
                        .storeUInt(1, 8) // 1 parameter
                        .storeUInt(1, 8)
                        .storeUInt(index, 64)
                        .storeRef(
                            CellBuilder.beginCell()
                                .endCell()
                        )
                        .endCell()
                ).toByteArray()
            )
            logger.debug("response: $response")
            require(response.exitCode == 0) { "Failed to run the method, exit code is ${response.exitCode}" }

            var loader = BagOfCells(response.result!!).roots.first().beginParse()
            loader.loadUInt(16) // skip whatever this is
            loader.loadUInt(8) // number of entries

            loader.loadUInt(8)
            loader.loadRef()
            var begin = loader.loadUInt(10).toInt()
            var end = loader.loadUInt(10).toInt()
            return Cell(loader.loadRef().bits.slice(begin..end)).beginParse().loadMsgAddr()!!
        }

        @JvmStatic
        suspend fun getItemContent(collection: MsgAddressInt, index: Int, individualContent: Cell): Cell {
            val liteClient: LiteApi by inject()

            val lastBlock = liteClient.getMasterchainInfo().last
            logger.debug("last block: $lastBlock")

            val accountId = LiteServerAccountId((collection as MsgAddressInt.AddrStd).workchain_id, collection.address)

            logger.debug("running method `get_nft_content` on ${collection.toString(userFriendly = true)}")
            val response = liteClient.runSmcMethod(
                0b00100, // we only care about the result
                lastBlock,
                accountId,
                68445L, // get_nft_content
                BagOfCells(
                    CellBuilder.beginCell()
                        .storeUInt(0, 16)
                        .storeUInt(2, 8) // 2 parameters
                        .storeUInt(3, 8)
                        .storeRef(
                            CellBuilder.beginCell()
                                .storeUInt(1, 8)
                                .storeUInt(index, 64)
                                .storeRef(
                                    CellBuilder.beginCell()
                                        .endCell()
                                )
                                .endCell()
                        )
                        .storeRef(individualContent)
                        .endCell()
                ).toByteArray()
            )
            logger.debug("response: $response")
            require(response.exitCode == 0) { "Failed to run the method, exit code is ${response.exitCode}" }

            var loader = BagOfCells(response.result!!).roots.first().beginParse()
            loader.loadUInt(16) // skip whatever this is
            loader.loadUInt(8) // number of entries


            loader.loadUInt(8) // type
            loader.loadRef()
            return loader.loadRef() // our content
        }

        @JvmStatic
        suspend fun getRoyaltyParameters(collection: MsgAddressInt): Pair<Float, MsgAddressInt>? {
            val liteClient: LiteApi by inject()
            val lastBlock = liteClient.getMasterchainInfo().last
            logger.debug("last block: $lastBlock")

            val accountId = LiteServerAccountId((collection as MsgAddressInt.AddrStd).workchain_id, collection.address)

            logger.debug("running method `get_nft_address_by_index` on ${collection.toString(userFriendly = true)}")
            val response = liteClient.runSmcMethod(
                0b00100, // we only care about the result
                lastBlock,
                accountId,
                85719L, // royalty_params
                BagOfCells(
                    CellBuilder.beginCell()
                        .storeUInt(0, 16)
                        .storeUInt(0, 8) // no parameters
                        .endCell()
                ).toByteArray()
            )
            logger.debug("response: $response")
            if (response.exitCode == 11) { // unknown error, its thrown when no such method exists
                // NFT Collection doesn't implement NFTRoyalty extension - its ok
                logger.debug("collection doesn't implement the NFTRoyalty extension")
                return null
            }

            require(response.exitCode == 0) {
                "Failed to run the method, exit code is ${response.exitCode}"
            }

            var loader = BagOfCells(response.result!!).roots.first().beginParse()
            loader.loadUInt(16) // skip whatever this is
            loader.loadUInt(8) // number of entries

            loader.loadUInt(8)
            var next = loader.loadRef()
            var begin = loader.loadUInt(10).toInt()
            var end = loader.loadUInt(10).toInt()
            val destination = Cell(loader.loadRef().bits.slice(begin..end)).beginParse().loadMsgAddr()
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val denominator = loader.loadUInt(64).toInt()
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val numerator = loader.loadUInt(64).toInt()

            return Pair(numerator.toFloat() / denominator, destination as MsgAddressInt)
        }
    }
}

data class NFTCollectionImpl(
    override val address: MsgAddressInt.AddrStd,
    val nextItemIndex: Int,
    val contentCell: Cell,
    override val owner: MsgAddressInt.AddrStd,
    val dispatcher: CoroutineContext = Dispatchers.Default
) : NFTCollection {
    override val size: Int
        get() = nextItemIndex // item indexes are zero-based

    private val _content: Deferred<NFTContent> = GlobalScope.async(dispatcher, start = CoroutineStart.LAZY) {
        NFTContent.parse(contentCell)
    }
    override val content: NFTContent by lazy {
        _content.getCompleted()
    }

    override fun toString(): String =
        "NFTCollection(address=$address, next_item_index=$nextItemIndex owner=$owner, content=$content)"
}
