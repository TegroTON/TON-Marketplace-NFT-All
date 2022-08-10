package money.tegro.market.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.timestamp
import org.ton.block.MsgAddressInt
import java.time.Instant

interface AccountModel : Entity<AccountModel> {
    var address: MsgAddressInt

    var updated: Instant

    companion object : Entity.Factory<AccountModel>()
}

object AccountTable : Table<AccountModel>("accounts") {
    val address = msgAddressInt("address").primaryKey().bindTo { it.address }
    val updated = timestamp("updated").bindTo { it.updated }
}

val Database.accounts get() = this.sequenceOf(AccountTable)
