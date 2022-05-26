package money.tegro.market.nft

import mu.KLogging
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
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
    suspend fun getItemAddress(index: Long) = NFTCollection.getItemAddress(this.address, index)

    suspend fun getItem(index: Long): NFTItem = NFTItem.fetch(getItemAddress(index))

    // Returns pair of percentage and destination for royalties
    suspend fun getRoyaltyParameters(): Pair<Float, MsgAddressInt>? =
        NFTCollection.getRoyaltyParameters(address)

    companion object : KoinComponent, KLogging() {
        @JvmStatic
        suspend fun fetch(address: MsgAddressInt.AddrStd): NFTCollection {
            val liteClient: LiteApi by inject()
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
        suspend fun getItemAddress(collection: MsgAddressInt.AddrStd, index: Long): MsgAddressInt.AddrStd {
            val liteClient: LiteApi by inject()

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
        suspend fun getItemContent(collection: MsgAddressInt.AddrStd, index: Long, individualContent: Cell): Cell {
            val liteClient: LiteApi by inject()

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

        @JvmStatic
        suspend fun getRoyaltyParameters(collection: MsgAddressInt.AddrStd): Pair<Float, MsgAddressInt>? {
            val liteClient: LiteApi by inject()
            val lastBlock = liteClient.getMasterchainInfo().last
            logger.debug("last block: $lastBlock")

            logger.debug("running method `get_nft_address_by_index` on ${collection.toString(userFriendly = true)}")
            val result = liteClient.runSmcMethod(
                0b100, // we only care about the result
                lastBlock,
                LiteServerAccountId(collection),
                "royalty_params"
            )

            logger.debug("response: $result")
            if (result.exitCode == 11) { // unknown error, its thrown when no such method exists
                // NFT Collection doesn't implement NFTRoyalty extension - its ok
                logger.debug("collection doesn't implement the NFTRoyalty extension")
                return null
            }

            require(result.exitCode == 0) {
                "Failed to run the method, exit code is ${result.exitCode}"
            }


            val numerator = (result[0] as VmStackValue.TinyInt).value
            val denominator = (result[1] as VmStackValue.TinyInt).value
            val destination = (result[2] as VmStackValue.Slice).toCellSlice().loadTlb(MsgAddressInt.tlbCodec())

            return Pair(numerator.toFloat() / denominator, destination as MsgAddressInt.AddrStd)
        }
    }
}
