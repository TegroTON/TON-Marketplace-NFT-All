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

sealed interface NFTItem {
    abstract val address: MsgAddressInt
    abstract val initialized: Boolean
    abstract val index: Int
    abstract val collection: NFTCollection?
    abstract val owner: MsgAddressInt
    abstract val content: NFTContent

    companion object : KoinComponent, KLogging() {
        @JvmStatic
        suspend fun fetch(
            address: MsgAddressInt
        ): NFTItem {
            val liteClient: LiteApi by inject()

            val lastBlock = liteClient.getMasterchainInfo().last
            logger.debug("last block: $lastBlock")

            val accountId = LiteServerAccountId((address as MsgAddressInt.AddrStd).workchain_id, address.address)
            logger.debug("running method `get_nft_data` on ${address.toString(userFriendly = true)}")
            val response = liteClient.runSmcMethod(
                0b100, // we only care about the result
                lastBlock,
                accountId,
                102351L, // get_nft_data
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

            loader.loadUInt(8) // type of the last entry, going backwards here
            var next = loader.loadRef()
            var contentCell = loader.loadRef()
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            var begin = loader.loadUInt(10).toInt()
            var end = loader.loadUInt(10).toInt()
            val owner = Cell(loader.loadRef().bits.slice(begin..end)).beginParse().loadMsgAddr()!!
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            begin = loader.loadUInt(10).toInt()
            end = loader.loadUInt(10).toInt()
            val collectionAddress = Cell(loader.loadRef().bits.slice(begin..end)).beginParse().loadMsgAddr()
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val index = loader.loadUInt(64).toInt()
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val initialized = loader.loadInt(64).toInt()

            return NFTItemImpl(
                address,
                initialized == -1,
                index,
                collectionAddress,
                owner,
                contentCell
            )
        }
    }
}

data class NFTItemImpl(
    override val address: MsgAddressInt.AddrStd,
    override val initialized: Boolean,
    override val index: Int,
    private val collectionAddress: MsgAddressInt.AddrStd?,
    override val owner: MsgAddressInt.AddrStd,
    val contentCell: Cell,
    val dispatcher: CoroutineContext = Dispatchers.Default
) : NFTItem {
    private val _collection: Deferred<NFTCollection?> = GlobalScope.async(dispatcher, start = CoroutineStart.LAZY) {
        if (collectionAddress != null) NFTCollection.fetch(collectionAddress!!) else null
    }
    override val collection: NFTCollection? by lazy {
        _collection.getCompleted()
    }

    private val _content: Deferred<NFTContent> = GlobalScope.async(dispatcher, start = CoroutineStart.LAZY) {
        if (collectionAddress != null) {
            NFTContent.parse(NFTCollection.getItemContent(collectionAddress!!, index, contentCell))
        } else {
            NFTContent.parse(contentCell)
        }
    }
    override val content: NFTContent by lazy {
        _content.getCompleted()
    }

    override fun toString(): String =
        "NFTItem(address=$address, initialized=$initialized, index=$index, collection=$collection, owner=$owner, content=$content)"
}

