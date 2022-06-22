package money.tegro.market.core.repository

import money.tegro.market.core.key.AddressKey
import money.tegro.market.core.model.BasicModel
import org.ton.block.MsgAddressIntStd
import reactor.core.publisher.Mono

interface BasicRepository<E : BasicModel> {
    fun existsByAddress(address: AddressKey): Boolean
    fun findByAddress(address: AddressKey): Mono<E>
}

fun <E : BasicModel> BasicRepository<E>.existsByAddressStd(address: MsgAddressIntStd) =
    existsByAddress(AddressKey.of(address))

fun <E : BasicModel> BasicRepository<E>.findByAddressStd(address: MsgAddressIntStd) =
    findByAddress(AddressKey.of(address))
