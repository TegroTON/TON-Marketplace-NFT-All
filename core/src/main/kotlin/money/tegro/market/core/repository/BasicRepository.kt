package money.tegro.market.core.repository

import money.tegro.market.core.model.BasicModel
import org.ton.block.MsgAddressIntStd
import reactor.core.publisher.Mono

interface BasicRepository<E : BasicModel> {
    fun existsByWorkchainAndAddress(workchain: Int, address: ByteArray): Boolean
    fun findByWorkchainAndAddress(workchain: Int, address: ByteArray): Mono<E>
}

fun <E : BasicModel> BasicRepository<E>.existsByAddressStd(address: MsgAddressIntStd) =
    existsByWorkchainAndAddress(address.workchainId, address.address.toByteArray())

fun <E : BasicModel> BasicRepository<E>.findByAddressStd(address: MsgAddressIntStd) =
    findByWorkchainAndAddress(address.workchainId, address.address.toByteArray())
