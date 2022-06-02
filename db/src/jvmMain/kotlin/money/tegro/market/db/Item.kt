package money.tegro.market.db

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.and
import org.ton.block.MsgAddressInt

class Item(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Item>(Items) {
        @JvmStatic
        fun find(item: MsgAddressInt.AddrStd) =
            this.find { (Items.workchain eq item.workchainId) and (Items.address eq item.address) }
    }

    private var rawWorkchain by Items.workchain
    private var rawAddress by Items.address

    var address: MsgAddressInt.AddrStd
        get() = MsgAddressInt.AddrStd(rawWorkchain, rawAddress)
        set(value) {
            rawWorkchain = value.workchainId
            rawAddress = value.address
        }

    var initialized: Boolean by Items.initialized
    var index: Long? by Items.index
    var collection: Collection? by Collection optionalReferencedOn Items.collection

    private var rawOwnerWorkchain by Items.ownerWorkchain
    private var rawOwnerAddress by Items.ownerAddress

    var owner: MsgAddressInt.AddrStd?
        get() = rawOwnerWorkchain?.let { workchain ->
            rawOwnerAddress?.let { address ->
                MsgAddressInt.AddrStd(
                    workchain,
                    address
                )
            }
        }
        set(value) {
            rawOwnerWorkchain = value?.workchainId
            rawOwnerAddress = value?.address
        }
}
