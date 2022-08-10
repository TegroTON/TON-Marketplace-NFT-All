package money.tegro.market.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.timestamp
import org.ton.bigint.BigInt
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

interface SaleModel : Entity<SaleModel> {
    var address: MsgAddressInt
    var marketplace: MsgAddress
    var item: MsgAddress
    var owner: MsgAddress
    var fullPrice: BigInt
    var marketplaceFee: BigInt
    var royalty: BigInt
    var royaltyDestination: MsgAddress
    var updated: Instant
}

object SaleTable : Table<SaleModel>("sales") {
    val address = msgAddressInt("address").primaryKey().bindTo { it.address }
    val marketplace = msgAddress("marketplace").bindTo { it.marketplace }
    val item = msgAddress("item").bindTo { it.item }
    val owner = msgAddress("owner").bindTo { it.owner }
    val fullPrice = numeric("full_price").bindTo { it.fullPrice }
    val marketplaceFee = numeric("marketplace_fee").bindTo { it.marketplaceFee }
    val royalty = numeric("royalty").bindTo { it.royalty }
    val royaltyDestination = msgAddress("royalty_destination").bindTo { it.royaltyDestination }
    val updated = timestamp("updated").bindTo { it.updated }
}

val Database.sales get() = this.sequenceOf(SaleTable)
