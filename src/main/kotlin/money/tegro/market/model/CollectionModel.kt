package money.tegro.market.model

import org.ktorm.database.Database
import org.ktorm.entity.Entity
import org.ktorm.entity.sequenceOf
import org.ktorm.schema.Table
import org.ktorm.schema.long
import org.ktorm.schema.text
import org.ktorm.schema.timestamp
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import java.time.Instant

interface CollectionModel : Entity<CollectionModel> {
    var address: MsgAddressInt
    var nextItemIndex: Long
    var owner: MsgAddress
    var name: String?
    var description: String?
    var image: String?
    var coverImage: String?
    var updated: Instant

    companion object : Entity.Factory<CollectionModel>()
}

object CollectionTable : Table<CollectionModel>("collections") {
    val address = msgAddressInt("address").primaryKey().bindTo { it.address }
    val nextItemIndex = long("next_item_index").bindTo { it.nextItemIndex }
    val owner = msgAddress("owner").bindTo { it.owner }
    val name = text("name").bindTo { it.name }
    val description = text("description").bindTo { it.description }
    val image = text("image").bindTo { it.image }
    val coverImage = text("cover_image").bindTo { it.coverImage }
    val updated = timestamp("updated").bindTo { it.updated }
}

val Database.collections get() = this.sequenceOf(CollectionTable)
