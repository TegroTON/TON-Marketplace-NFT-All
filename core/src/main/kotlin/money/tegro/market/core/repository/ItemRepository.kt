package money.tegro.market.nightcrawler

import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorCrudRepository
import money.tegro.market.core.model.ItemData
import org.ton.block.MsgAddressIntStd

@R2dbcRepository(dialect = Dialect.H2)
abstract class ItemRepository : ReactorCrudRepository<ItemData, Long> {
    abstract fun existsByWorkchainAndAddress(workchain: Int, address: ByteArray): Boolean

    fun existsByAddressStd(address: MsgAddressIntStd) =
        existsByWorkchainAndAddress(address.workchainId, address.address.toByteArray())
}
