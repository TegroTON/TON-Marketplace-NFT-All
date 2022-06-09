package money.tegro.market.db

import org.jetbrains.exposed.dao.id.LongIdTable

object ItemAttributesTable : LongIdTable("item_attributes") {
    val item = reference("item", ItemsTable)
    val trait = text("trait")
    val value = text("value")
}
