package money.tegro.market.core.repository

import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.BasicModel
import org.ton.block.AddrStd
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BasicRepository<E : BasicModel> {
    fun existsByAddress(address: AddressKey): Boolean
    fun findByAddress(address: AddressKey): Mono<E>

    fun findByOwner(owner: AddressKey): Flux<E>
}

fun <E : BasicModel> BasicRepository<E>.existsByAddressStd(address: AddrStd) =
    existsByAddress(AddressKey.of(address))

fun <E : BasicModel> BasicRepository<E>.findByAddressStd(address: AddrStd) =
    findByAddress(AddressKey.of(address))

fun <E : BasicModel> BasicRepository<E>.findByOwnerStd(owner: AddrStd) =
    findByOwner(AddressKey.of(owner))
