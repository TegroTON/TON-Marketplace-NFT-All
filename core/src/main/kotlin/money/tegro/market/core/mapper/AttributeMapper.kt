package money.tegro.market.core.mapper

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.model.AttributeModel
import reactor.core.publisher.Mono

@Singleton
class AttributeMapper {
    fun map(it: AttributeModel): Mono<Pair<String, String>> = mono { it.trait to it.value }
}
