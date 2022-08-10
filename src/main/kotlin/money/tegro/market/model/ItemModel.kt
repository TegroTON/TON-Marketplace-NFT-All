package money.tegro.market.core.model

import money.tegro.market.model.msgAddress
import money.tegro.market.model.msgAddressInt
import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.jackson.json
import org.ktorm.schema.*
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant


interface ItemModel : Entity<ItemModel> {
    var address: MsgAddressInt
    var initialized: Boolean
    var index: Long
    var collection: MsgAddress
    var owner: MsgAddress
    var name: String?
    var description: String?
    var image: String?
    var attributes: Map<String, String>?
    var updated: Instant

    companion object : Entity.Factory<ItemModel>()
}

object ItemTable : Table<ItemModel>("items") {
    val address = msgAddressInt("address").primaryKey().bindTo { it.address }
    val initialized = boolean("initialized").bindTo { it.initialized }
    val index = long("index").bindTo { it.index }
    val collection = msgAddress("collection").bindTo { it.collection }
    val owner = msgAddress("owner").bindTo { it.owner }
    val name = text("name").bindTo { it.name }
    val description = text("description").bindTo { it.description }
    val image = text("image").bindTo { it.image }
    val attributes = json<Map<String, String>>("attributes").bindTo { it.attributes }
    val updated = timestamp("updated").bindTo { it.updated }
}

val Database.items get() = this.sequenceOf(ItemTable)
