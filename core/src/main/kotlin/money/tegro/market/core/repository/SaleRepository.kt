package money.tegro.market.core.repository

import io.micronaut.data.annotation.Id
import io.micronaut.data.model.query.builder.sql.Dialect
import io.micronaut.data.r2dbc.annotation.R2dbcRepository
import io.micronaut.data.repository.reactive.ReactorPageableRepository
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTSale
import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.SaleModel
import org.ton.block.AddrStd
import reactor.core.publisher.Mono
import java.time.Instant
import javax.transaction.Transactional

@R2dbcRepository(dialect = Dialect.H2)
abstract class SaleRepository : ReactorPageableRepository<SaleModel, AddressKey> {
    fun findById(address: AddrStd) = findById(AddressKey.of(address))
    abstract fun findByItem(item: AddressKey): Mono<SaleModel>

    abstract fun findByIdForUpdate(id: AddressKey): Mono<SaleModel>

    abstract fun update(
        @Id address: AddressKey,
        marketplace: AddressKey,
        item: AddressKey,
        owner: AddressKey,
        fullPrice: Long,
        marketplaceFee: Long,
        royalty: Long?,
        royaltyDestination: AddressKey?,
        updated: Instant = Instant.now(),
    )

    abstract fun save(
        @Id address: AddressKey,
        marketplace: AddressKey,
        item: AddressKey,
        owner: AddressKey,
        fullPrice: Long,
        marketplaceFee: Long,
        royalty: Long?,
        royaltyDestination: AddressKey?,
        updated: Instant = Instant.now(),
        discovered: Instant = Instant.now()
    ): Mono<SaleModel>

    fun update(address: AddressKey, sale: NFTSale, updated: Instant = Instant.now()) =
        update(
            address,
            AddressKey.of(sale.marketplace),
            AddressKey.of(sale.item),
            AddressKey.of(sale.owner),
            sale.price,
            sale.marketplaceFee,
            sale.royalty,
            sale.royaltyDestination?.let { AddressKey.of(it) },
            updated
        )

    fun save(
        address: AddressKey,
        sale: NFTSale,
        updated: Instant = Instant.now(),
        discovered: Instant = Instant.now()
    ): Mono<SaleModel> =
        save(
            address,
            AddressKey.of(sale.marketplace),
            AddressKey.of(sale.item),
            AddressKey.of(sale.owner),
            sale.price,
            sale.marketplaceFee,
            sale.royalty,
            sale.royaltyDestination?.let { AddressKey.of(it) },
            updated,
            discovered
        )

    @Transactional
    fun upsert(address: AddressKey, sale: NFTSale, updated: Instant = Instant.now()) = mono {
        (findByIdForUpdate(address).awaitSingleOrNull()?.let {
            update(address, sale, updated)
            findByItem(address)
        } ?: run {
            save(address, sale, updated)
        }).awaitSingleOrNull()
    }
}

