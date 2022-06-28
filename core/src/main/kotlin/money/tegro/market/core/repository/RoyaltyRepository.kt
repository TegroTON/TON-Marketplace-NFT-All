package money.tegro.market.core.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTRoyalty
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.RoyaltyModel
import org.ton.block.AddrStd
import reactor.core.publisher.Mono
import java.time.Instant
import javax.transaction.Transactional

@R2dbcRepository(dialect = Dialect.H2)
abstract class RoyaltyRepository : ReactorPageableRepository<RoyaltyModel, AddressKey> {
    fun findById(address: AddrStd) = findById(AddressKey.of(address))
    abstract fun existsByAddress(address: AddressKey): Mono<Boolean>
    fun existsByAddress(address: AddrStd) = existsByAddress(AddressKey.of(address))

    abstract fun findByIdForUpdate(id: AddressKey): Mono<RoyaltyModel>

    abstract fun update(
        @Id address: AddressKey,
        numerator: Int,
        denominator: Int,
        destination: AddressKey,
        updated: Instant = Instant.now()
    )

    fun update(address: AddressKey, royalty: NFTRoyalty, updated: Instant = Instant.now()) =
        update(
            address,
            royalty.numerator,
            royalty.denominator,
            AddressKey.of(royalty.destination),
            updated
        )

    abstract fun save(
        @Id address: AddressKey,
        numerator: Int,
        denominator: Int,
        destination: AddressKey,
        updated: Instant = Instant.now(),
        discovered: Instant = Instant.now()
    ): Mono<RoyaltyModel>

    fun save(
        address: AddressKey,
        royalty: NFTRoyalty,
        updated: Instant = Instant.now(),
        discovered: Instant = Instant.now()
    ) =
        save(
            address,
            royalty.numerator,
            royalty.denominator,
            AddressKey.of(royalty.destination),
            updated,
            discovered
        )

    @Transactional
    fun upsert(address: AddressKey, royalty: NFTRoyalty, updated: Instant = Instant.now()) = mono {
        (findByIdForUpdate(address).awaitSingleOrNull()?.let {
            update(address, royalty, updated)
            findById(address)
        } ?: run {
            save(address, royalty, updated)
        }).awaitSingleOrNull()
    }
}
