package money.tegro.market.nightcrawler

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorCrudRepository
import money.tegro.market.core.model.CollectionData
import org.ton.block.MsgAddressIntStd
import reactor.core.publisher.Mono
import java.time.Instant

@R2dbcRepository(dialect = Dialect.H2)
abstract class CollectionRepository : ReactorCrudRepository<CollectionData, Long> {
    abstract fun existsByWorkchainAndAddress(workchain: Int, address: ByteArray): Boolean

    fun existsByAddressStd(address: MsgAddressIntStd) =
        existsByWorkchainAndAddress(address.workchainId, address.address.toByteArray())

    abstract fun findByWorkchainAndAddress(workchain: Int, address: ByteArray): Mono<CollectionData>

    fun findByAddressStd(address: MsgAddressIntStd) =
        findByWorkchainAndAddress(address.workchainId, address.address.toByteArray())

    abstract fun update(
        @Id id: Long,
        nextItemIndex: Long?,
        ownerWorkchain: Int?,
        ownerAddress: ByteArray?,
        content: ByteArray?,
        modified: Instant? = Instant.now()
    ): Unit

    abstract fun update(
        @Id id: Long,
        name: String?,
        description: String?,
        image: String?,
        imageData: ByteArray?,
        coverImage: String?,
        coverImageData: ByteArray?,
        modified: Instant? = Instant.now()
    ): Unit

    abstract fun update(
        @Id id: Long,
        numerator: Int?,
        denominator: Int?,
        destinationWorkchain: Int?,
        destinationAddress: ByteArray?,
        modified: Instant? = Instant.now()
    ): Unit
}
