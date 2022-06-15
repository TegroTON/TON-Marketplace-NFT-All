package money.tegro.market.nft

import mu.KLogging
import org.ton.block.*
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.hex
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

interface NFTItem {
    val address: MsgAddressIntStd
    val index: Long
    val owner: MsgAddressIntStd
    val individualContent: Cell

    suspend fun content(liteApi: LiteApi): Cell

    companion object : KLogging() {
        private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        @JvmStatic
        suspend fun of(
            address: MsgAddressIntStd,
            liteApi: LiteApi,
        ): NFTItem? {
            val referenceBlock = liteApi.getMasterchainInfo().last

            logger.debug("running method `get_nft_data` on ${address.toString(userFriendly = true)}")
            val result = liteApi.runSmcMethod(0b100, referenceBlock, LiteServerAccountId(address), "get_nft_data")

            logger.debug("response: $result")
            if (result.exitCode != 0) {
                logger.warn("Method exit code was ${result.exitCode}. NFT is most likely not initialized")
                return null
            }

            if ((result[0] as VmStackValue.TinyInt).value == -1L) {
                val index = (result[1] as VmStackValue.TinyInt).value
                val collection = (result[2] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(msgAddressCodec) as? MsgAddressIntStd
                val owner = (result[3] as VmStackValue.Slice).toCellSlice()
                    .loadTlb(msgAddressCodec) as MsgAddressIntStd
                val content = (result[4] as VmStackValue.Cell).cell

                return if (collection == null) {
                    NFTDeployedStandaloneItem(address, index, owner, content)
                } else {
                    NFTDeployedCollectionItem(address, index, collection, owner, content)
                }
            } else {
                return null
            }
        }
    }
}

interface NFTDeployedItem : NFTItem

data class NFTDeployedStandaloneItem(
    override val address: MsgAddressIntStd,
    override val index: Long,
    override val owner: MsgAddressIntStd,
    override val individualContent: Cell,
) : NFTDeployedItem {
    override suspend fun content(liteApi: LiteApi) = individualContent
}

data class NFTDeployedCollectionItem(
    override val address: MsgAddressIntStd,
    override val index: Long,
    val collection: MsgAddressIntStd,
    override val owner: MsgAddressIntStd,
    override val individualContent: Cell,
) : NFTDeployedItem {
    override suspend fun content(liteApi: LiteApi): Cell {
        val referenceBlock = liteApi.getMasterchainInfo().last

        logger.debug("running method `get_nft_content` on ${collection.toString(userFriendly = true)}")
        val result = liteApi.runSmcMethod(
            0b100,
            referenceBlock,
            LiteServerAccountId(collection),
            "get_nft_content",
            VmStackValue.TinyInt(index),
            VmStackValue.Cell(individualContent)
        )

        logger.debug("response: $result")
        require(result.exitCode == 0) { "Failed to run the method, exit code is ${result.exitCode}" }

        return (result.first() as VmStackValue.Cell).cell
    }

    companion object : KLogging()
}

interface NFTStubItem : NFTItem {
    companion object {
        val NFT_ITEM_CODE = BagOfCells.of(
            hex(
                "B5EE9C7241020D010001D0000114FF00F4A413F4BCF2C80B0102016202030202CE04050009A11F9FE00502012006070201200B0C02D70C8871C02497C0F83434C0C05C6C2497C0F83E903E900C7E800C5C75C87E800C7E800C3C00812CE3850C1B088D148CB1C17CB865407E90350C0408FC00F801B4C7F4CFE08417F30F45148C2EA3A1CC840DD78C9004F80C0D0D0D4D60840BF2C9A884AEB8C097C12103FCBC20080900113E910C1C2EBCB8536001F65135C705F2E191FA4021F001FA40D20031FA00820AFAF0801BA121945315A0A1DE22D70B01C300209206A19136E220C2FFF2E192218E3E821005138D91C85009CF16500BCF16712449145446A0708010C8CB055007CF165005FA0215CB6A12CB1FCB3F226EB39458CF17019132E201C901FB00104794102A375BE20A00727082108B77173505C8CBFF5004CF1610248040708010C8CB055007CF165005FA0215CB6A12CB1FCB3F226EB39458CF17019132E201C901FB000082028E3526F0018210D53276DB103744006D71708010C8CB055007CF165005FA0215CB6A12CB1FCB3F226EB39458CF17019132E201C901FB0093303234E25502F003003B3B513434CFFE900835D27080269FC07E90350C04090408F80C1C165B5B60001D00F232CFD633C58073C5B3327B5520BF75041B"
            )
        ).roots.first()
    }
}

data class NFTStubStandaloneItem(
    override val owner: MsgAddressIntStd,
    override val individualContent: Cell,
    val code: Cell = NFTStubItem.NFT_ITEM_CODE,
    val workchainId: Int = owner.workchainId,
    override val index: Long = 0L,
) : NFTStubItem {
    private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }
    private val stateInitCodec by lazy { StateInit.tlbCodec() }

    override val address: MsgAddressIntStd
        get() = MsgAddressIntStd(workchainId, CellBuilder.createCell { storeTlb(stateInitCodec, stateInit()) }.hash())

    override suspend fun content(liteApi: LiteApi) = individualContent

    fun stateInit() = StateInit(createCode(), createData())

    fun createCode() = code

    fun createData(): Cell = CellBuilder.createCell {
        storeUInt(index, 64)
        storeTlb(msgAddressCodec, MsgAddressExtNone)
        storeTlb(msgAddressCodec, owner)
        storeRef(individualContent)
    }
}
