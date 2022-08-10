package money.tegro.market.core.model

import money.tegro.market.model.msgAddress
import money.tegro.market.model.msgAddressInt
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.timestamp
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

interface RoyaltyModel : Entity<RoyaltyModel> {
    var address: MsgAddressInt
    var numerator: Int
    var denominator: Int
    var destination: MsgAddress
    var updated: Instant

    companion object : Entity.Factory<RoyaltyModel>()
}

object RoyaltyTable : Table<RoyaltyModel>("royalties") {
    val address = msgAddressInt("address").primaryKey().bindTo { it.address }
    val numerator = int("numerator").bindTo { it.numerator }
    val denominator = int("denominator").bindTo { it.denominator }
    var destination = msgAddress("destination").bindTo { it.destination }
    val updated = timestamp("updated").bindTo { it.updated }
}

val Database.royalties get() = this.sequenceOf(RoyaltyTable)
