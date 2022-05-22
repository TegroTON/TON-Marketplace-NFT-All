package money.tegro.market.nft_tool

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.ton.block.MsgAddressInt
import org.ton.cell.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient

data class NFTCollection(
    val address: MsgAddressInt.AddrStd,
    val nextItemIndex: Int,
    val content: NFTContent,
    val owner: MsgAddressInt.AddrStd,
) {
    override fun toString(): String =
        "NFTCollection(address=$address, next_item_index=$nextItemIndex owner=$owner, content=$content)"

    suspend fun getNFT(index: Int): NFTItem =
        NFTItem.fetch(getNFTAddress(address, index))

    // Returns pair of percentage and destination for royalties
    suspend fun getRoyaltyParameters(): Pair<Float, MsgAddressInt.AddrStd>? =
        Companion.getRoyaltyParameters(address)


    companion object : KoinComponent {
        @JvmStatic
        suspend fun fetch(
            address: MsgAddressInt.AddrStd
        ): NFTCollection {
            val liteClient: LiteClient by inject()
            val lastBlock = liteClient.getMasterchainInfo().last

            val accountId = LiteServerAccountId(address.workchain_id, address.address)

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
            require(response.exitCode == 0) { "Failed to run the method, exit code is ${response.exitCode}" }
            var loader = BagOfCells(response.result!!).roots.first().beginParse()
            loader.loadUInt(16) // skip whatever this is
            loader.loadUInt(8) // number of entries

            loader.loadUInt(8)
            var next = loader.loadRef()
            var begin = loader.loadUInt(10).toInt()
            var end = loader.loadUInt(10).toInt()
            val owner = toAddress(Cell(loader.loadRef().bits.slice(begin..end)).beginParse())!!
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val contentCell = loader.loadRef()
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val nextItemIndex = loader.loadUInt(64).toInt()

            return NFTCollection(
                address,
                nextItemIndex,
                NFTContent.parse(contentCell),
                owner
            )
        }

        @JvmStatic
        suspend fun getNFTAddress(
            collection: MsgAddressInt.AddrStd,
            index: Int
        ): MsgAddressInt.AddrStd {
            val liteClient: LiteClient by inject()
            val lastBlock = liteClient.getMasterchainInfo().last

            val accountId = LiteServerAccountId(collection.workchain_id, collection.address)

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
            require(response.exitCode == 0) { "Failed to run the method, exit code is ${response.exitCode}" }
            var loader = BagOfCells(response.result!!).roots.first().beginParse()
            loader.loadUInt(16) // skip whatever this is
            loader.loadUInt(8) // number of entries

            loader.loadUInt(8)
            loader.loadRef()
            var begin = loader.loadUInt(10).toInt()
            var end = loader.loadUInt(10).toInt()
            return toAddress(Cell(loader.loadRef().bits.slice(begin..end)).beginParse())!!
        }

        @JvmStatic
        suspend fun getRoyaltyParameters(collection: MsgAddressInt.AddrStd): Pair<Float, MsgAddressInt.AddrStd>? {
            val liteClient: LiteClient by inject()
            val lastBlock = liteClient.getMasterchainInfo().last

            val accountId = LiteServerAccountId(collection.workchain_id, collection.address)

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
            if (response.exitCode == 11) { // unknown error, its thrown when no such method exists
                // NFT Collection doesn't implement NFTRoyalty extension - its ok
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
            val destination = toAddress(Cell(loader.loadRef().bits.slice(begin..end)).beginParse())!!
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val denominator = loader.loadUInt(64).toInt()
            loader = next.beginParse()

            loader.loadUInt(8)
            next = loader.loadRef()
            val numerator = loader.loadUInt(64).toInt()

            return Pair(numerator.toFloat() / denominator, destination)
        }


        @JvmStatic
        suspend fun getNFTContent(
            collection: MsgAddressInt.AddrStd, index: Int, individualContent: Cell
        ): Cell {
            val liteClient: LiteClient by inject()
            val lastBlock = liteClient.getMasterchainInfo().last

            val accountId = LiteServerAccountId(collection.workchain_id, collection.address)

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
            require(response.exitCode == 0) { "Failed to run the method, exit code is ${response.exitCode}" }
            var loader = BagOfCells(response.result!!).roots.first().beginParse()
            loader.loadUInt(16) // skip whatever this is
            loader.loadUInt(8) // number of entries


            loader.loadUInt(8) // type
            loader.loadRef()
            return loader.loadRef() // our content
        }
    }
}