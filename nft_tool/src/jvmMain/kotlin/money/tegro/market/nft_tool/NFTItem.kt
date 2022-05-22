package money.tegro.market.nft_tool

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ton.block.MsgAddressInt
import org.ton.cell.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient

data class NFTItem(
    val address: MsgAddressInt.AddrStd,
    val initialized: Boolean,
    val index: Int,
    val collection: NFTCollection?,
    val owner: MsgAddressInt.AddrStd,
    val content: NFTContent
) {
    override fun toString(): String =
        "NFTItem(address=$address, initialized=$initialized, index=$index, collection=$collection, owner=$owner, content=$content)"

    companion object Factory : KoinComponent {
        @JvmStatic
        suspend fun fetch(
            address: MsgAddressInt.AddrStd
        ): NFTItem {
            val liteClient: LiteClient by inject()

            val lastBlock = liteClient.getMasterchainInfo().last

            val accountId = LiteServerAccountId(address.workchain_id, address.address)

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
            val owner = toAddress(Cell(loader.loadRef().bits.slice(begin..end)).beginParse())!!
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            begin = loader.loadUInt(10).toInt()
            end = loader.loadUInt(10).toInt()
            val collectionAddress = toAddress(Cell(loader.loadRef().bits.slice(begin..end)).beginParse())
            val collection = if (collectionAddress != null) NFTCollection.fetch(collectionAddress) else null
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val index = loader.loadUInt(64).toInt()
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val initialized = loader.loadInt(64).toInt()

            if (initialized == -1 && collection != null) {
                contentCell = NFTCollection.getNFTContent(collectionAddress!!, index, contentCell)
            }

            return NFTItem(
                address,
                initialized == -1,
                index,
                collection,
                owner,
                NFTContent.parse(contentCell)
            )
        }
    }
}