package money.tegro.market.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and

class ItemAttributeEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<ItemAttributeEntity>(ItemAttributesTable) {
        @JvmStatic
        fun find(item: ItemEntity) =
            this.find { ItemAttributesTable.item eq item.id }

        @JvmStatic
        fun find(item: ItemEntity, trait: String) =
            this.find { (ItemAttributesTable.item eq item.id) and (ItemAttributesTable.trait eq trait) }
    }

    var item by ItemEntity referencedOn ItemAttributesTable.item
    var trait by ItemAttributesTable.trait
    var value by ItemAttributesTable.value
}
