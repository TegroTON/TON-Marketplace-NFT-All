package money.tegro.market.nightcrawler

import com.badoo.reaktive.observable.observable
import money.tegro.market.db.CollectionEntity
import money.tegro.market.db.ItemEntity
import org.jetbrains.exposed.sql.transactions.transaction
import org.ton.block.MsgAddressIntStd

fun databaseCollections() =
    observable<MsgAddressIntStd> { emitter ->
        transaction {
            CollectionEntity.all().forEach {
                emitter.onNext(MsgAddressIntStd(it.workchain, it.address))
            }
        }
        emitter.onComplete()
    }

fun databaseItems(filter: (ItemEntity) -> Boolean = { true }) =
    observable<MsgAddressIntStd> { emitter ->
        transaction {
            ItemEntity.all().filter(filter).forEach {
                emitter.onNext(MsgAddressIntStd(it.workchain, it.address))
            }
        }
        emitter.onComplete()
    }
