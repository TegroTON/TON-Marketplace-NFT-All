package money.tegro.market.contract

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import money.tegro.market.serializer.CellSerializer
import money.tegro.market.serializer.MsgAddressSerializer
import mu.KLogging
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrStd
import org.ton.block.MsgAddress
import org.ton.block.StateInit
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.hex
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

data class ItemContract(
    val initialized: Boolean,
    val index: ULong,
    @JsonSerialize(using = MsgAddressSerializer::class)
    val collection: MsgAddress,
    @JsonSerialize(using = MsgAddressSerializer::class)
    val owner: MsgAddress,
    @JsonSerialize(using = CellSerializer::class)
    val individualContent: Cell,

    val workchain_id: Int = 0,
    val code: Cell = NFT_ITEM_CODE,
) {
    fun address(stateInit: StateInit = stateInit()) =
        AddrStd(workchain_id, CellBuilder.createCell { storeTlb(StateInit, stateInit) }.hash())

    fun stateInit(data: Cell = createData()) = StateInit(code, createData())

    fun createData() = CellBuilder.createCell {
        storeUInt64(index)
        storeTlb(MsgAddress, collection)
        storeTlb(MsgAddress, owner)
        storeRef(individualContent)
    }

    companion object : KLogging() {
        val NFT_ITEM_CODE =
            BagOfCells(hex("B5EE9C7241020D010001D0000114FF00F4A413F4BCF2C80B0102016202030202CE04050009A11F9FE00502012006070201200B0C02D70C8871C02497C0F83434C0C05C6C2497C0F83E903E900C7E800C5C75C87E800C7E800C3C00812CE3850C1B088D148CB1C17CB865407E90350C0408FC00F801B4C7F4CFE08417F30F45148C2EA3A1CC840DD78C9004F80C0D0D0D4D60840BF2C9A884AEB8C097C12103FCBC20080900113E910C1C2EBCB8536001F65135C705F2E191FA4021F001FA40D20031FA00820AFAF0801BA121945315A0A1DE22D70B01C300209206A19136E220C2FFF2E192218E3E821005138D91C85009CF16500BCF16712449145446A0708010C8CB055007CF165005FA0215CB6A12CB1FCB3F226EB39458CF17019132E201C901FB00104794102A375BE20A00727082108B77173505C8CBFF5004CF1610248040708010C8CB055007CF165005FA0215CB6A12CB1FCB3F226EB39458CF17019132E201C901FB000082028E3526F0018210D53276DB103744006D71708010C8CB055007CF165005FA0215CB6A12CB1FCB3F226EB39458CF17019132E201C901FB0093303234E25502F003003B3B513434CFFE900835D27080269FC07E90350C04090408F80C1C165B5B60001D00F232CFD633C58073C5B3327B5520BF75041B")).roots.first()

        @JvmStatic
        suspend fun of(
            address: AddrStd,
            liteClient: LiteClient,
            referenceBlock: TonNodeBlockIdExt? = null
        ): ItemContract =
            liteClient.runSmcMethod(
                LiteServerAccountId(address),
                referenceBlock ?: liteClient.getLastBlockId(),
                "get_nft_data"
            ).toMutableVmStack().let {
                ItemContract(
                    initialized = it.popTinyInt() == -1L,
                    index = it.popTinyInt().toULong(), // TODO
                    collection = it.popSlice().loadTlb(MsgAddress),
                    owner = it.popSlice().loadTlb(MsgAddress),
                    individualContent = it.popCell(),
                )
            }
    }
}
