package money.tegro.market.nft

import mu.KLogging
import org.ton.block.MsgAddressInt
import org.ton.block.VmStackValue
import org.ton.block.tlb.tlbCodec
import org.ton.cell.Cell
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

data class NFTCollection(
    val address: MsgAddressInt.AddrStd,
    val size: Long,
    val content: Cell,
    val owner: MsgAddressInt.AddrStd
) {
    companion object : KLogging() {
        @JvmStatic
        suspend fun fetch(liteClient: LiteApi, address: MsgAddressInt.AddrStd): NFTCollection {
            val lastBlock = liteClient.getMasterchainInfo().last
            logger.debug("last block: $lastBlock")

            logger.debug("running method `get_collection_data` on ${address.toString(userFriendly = true)}")
            val result = liteClient.runSmcMethod(
                0b100, // we only care about the result
                lastBlock,
                LiteServerAccountId(address),
                "get_collection_data"
            )

            logger.debug("response: $result")
            require(result.exitCode == 0) { "Failed to run the method, exit code is ${result.exitCode}" }

            return NFTCollection(
                address,
                (result[0] as VmStackValue.TinyInt).value,
                (result[1] as VmStackValue.Cell).cell,
                (result[2] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(MsgAddressInt.tlbCodec()) as MsgAddressInt.AddrStd
            )
        }

        @JvmStatic
        suspend fun getItemAddress(
            liteClient: LiteApi,
            collection: MsgAddressInt.AddrStd,
            index: Long
        ): MsgAddressInt.AddrStd {
            val lastBlock = liteClient.getMasterchainInfo().last
            logger.debug("last block: $lastBlock")

            logger.debug("running method `get_nft_address_by_index` on ${collection.toString(userFriendly = true)}")
            val result = liteClient.runSmcMethod(
                0b100, // we only care about the result
                lastBlock,
                LiteServerAccountId(collection),
                "get_nft_address_by_index",
                VmStackValue.TinyInt(index)
            )

            logger.debug("response: $result")
            require(result.exitCode == 0) { "Failed to run the method, exit code is ${result.exitCode}" }

            return (result.first() as VmStackValue.Slice).toCellSlice()
                .loadTlb(MsgAddressInt.tlbCodec()) as MsgAddressInt.AddrStd
        }

        @JvmStatic
        suspend fun getItemContent(
            liteClient: LiteApi,
            collection: MsgAddressInt.AddrStd,
            index: Long,
            individualContent: Cell
        ): Cell {
            val lastBlock = liteClient.getMasterchainInfo().last
            logger.debug("last block: $lastBlock")

            logger.debug("running method `get_nft_content` on ${collection.toString(userFriendly = true)}")
            val result = liteClient.runSmcMethod(
                0b00100, // we only care about the result
                lastBlock,
                LiteServerAccountId(collection),
                "get_nft_content",
                VmStackValue.TinyInt(index),
                VmStackValue.Cell(individualContent)
            )

            logger.debug("response: $result")
            require(result.exitCode == 0) { "Failed to run the method, exit code is ${result.exitCode}" }

            return (result.first() as VmStackValue.Cell).cell
        }
    }
}

suspend fun LiteApi.getNFTCollection(address: MsgAddressInt.AddrStd) = NFTCollection.fetch(this, address)

suspend fun LiteApi.getNFTCollectionItem(collection: MsgAddressInt.AddrStd, index: Long) =
    NFTCollection.getItemAddress(this, collection, index)

suspend fun LiteApi.getNFTCollectionItem(collection: NFTCollection, index: Long) =
    this.getNFTItem(NFTCollection.getItemAddress(this, collection.address, index))

suspend fun LiteApi.getNFTCollectionItemContent(
    collection: MsgAddressInt.AddrStd,
    index: Long,
    individualContent: Cell
) =
    NFTCollection.getItemContent(this, collection, index, individualContent)

suspend fun LiteApi.getNFTCollectionRoyalties(address: MsgAddressInt.AddrStd) =
    NFT.getRoyaltyParameters(this, address)
