package money.tegro.market.nightcrawler

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.ton.bitstring.BitString
import org.ton.block.MsgAddressInt
import org.ton.block.tlb.tlbCodec
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

object Collections : LongIdTable("nft_collections") {
    val address = binary("address").uniqueIndex()
    val owner = binary("owner")
    val nextItemIndex = long("next_item_index")
    val itemCode = binary("item_code")
    val royaltyNumerator = integer("royalty_numerator").nullable()
    val royaltyDenominator = integer("royalty_denominator").nullable()
    val royaltyDestination = binary("royalty_destination").nullable()
}

class Collection(id: EntityID<Long>) : LongEntity(id) {
    private val msgAddressIntCodec by lazy { MsgAddressInt.tlbCodec() }

    companion object : LongEntityClass<Collection>(Collections)

    var address: MsgAddressInt.AddrStd by Collections.address.transform(
        { CellBuilder.createCell { storeTlb(msgAddressIntCodec, it) }.bits.toByteArray() },
        { Cell(BitString.of(it)).parse { loadTlb(msgAddressIntCodec) as MsgAddressInt.AddrStd } }
    )
    var owner: MsgAddressInt.AddrStd by Collections.owner.transform(
        { CellBuilder.createCell { storeTlb(msgAddressIntCodec, it) }.bits.toByteArray() },
        { Cell(BitString.of(it)).parse { loadTlb(msgAddressIntCodec) as MsgAddressInt.AddrStd } }
    )
    var nextItemIndex: Long by Collections.nextItemIndex
    var itemCode: BagOfCells by Collections.itemCode.transform({ it.toByteArray() }, { BagOfCells(it) })

    var royaltyNumerator: Int? by Collections.royaltyNumerator
    var royaltyDenominator: Int? by Collections.royaltyDenominator
    val royalty: Float? by lazy { royaltyNumerator?.toFloat()?.div(royaltyDenominator ?: 1) }
    var royaltyDestination: MsgAddressInt.AddrStd? by Collections.royaltyDestination.transform(
        { it?.let { CellBuilder.createCell { storeTlb(msgAddressIntCodec, it) }.bits.toByteArray() } },
        { it?.let { Cell(BitString.of(it)).parse { loadTlb(msgAddressIntCodec) as MsgAddressInt.AddrStd } } }
    )

    val items by Item optionalReferrersOn Items.collection
}

object Items : LongIdTable("nft_items") {
    val address = binary("address").uniqueIndex()
    val initialized = bool("initialized")
    val index = long("index").nullable()
    val collection = reference("collection", Collections).nullable()
    val owner = binary("owner").nullable()
}

class Item(id: EntityID<Long>) : LongEntity(id) {
    private val msgAddressIntCodec by lazy { MsgAddressInt.tlbCodec() }

    companion object : LongEntityClass<Item>(Items)

    var address: MsgAddressInt.AddrStd by Items.address.transform(
        { CellBuilder.createCell { storeTlb(msgAddressIntCodec, it) }.bits.toByteArray() },
        { Cell(BitString.of(it)).parse { loadTlb(msgAddressIntCodec) as MsgAddressInt.AddrStd } }
    )
    var initialized: Boolean by Items.initialized
    var index: Long? by Items.index
    var collection: Collection? by Collection optionalReferencedOn Items.collection
    var owner: MsgAddressInt.AddrStd? by Items.owner.transform(
        { it?.let { CellBuilder.createCell { storeTlb(msgAddressIntCodec, it) }.bits.toByteArray() } },
        { it?.let { Cell(BitString.of(it)).parse { loadTlb(msgAddressIntCodec) as MsgAddressInt.AddrStd } } }
    )
}

suspend fun main(args: Array<String>) {
}
