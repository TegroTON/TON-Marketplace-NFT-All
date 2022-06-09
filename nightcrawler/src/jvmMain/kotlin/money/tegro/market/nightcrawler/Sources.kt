package money.tegro.market.nightcrawler

import com.badoo.reaktive.observable.observable
import money.tegro.market.db.CollectionEntity
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

