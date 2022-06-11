package money.tegro.market.db

import org.ton.block.MsgAddressIntStd

class ItemRepository : BaseRepository<ItemEntity>() {
    override val resourceClass get() = ItemEntity::class.java

    fun find(workchain: Int, address: ByteArray) = criteria { query, entity ->
        query
            .addWhere(equal(entity.get(ItemEntity::workchain), workchain))
            .addWhere(equal(entity.get(ItemEntity::address), address))
    }.findOne()

    fun find(address: MsgAddressIntStd) = find(address.workchainId, address.address.toByteArray())
}
