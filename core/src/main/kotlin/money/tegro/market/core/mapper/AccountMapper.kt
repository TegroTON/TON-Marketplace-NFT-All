package money.tegro.market.core.mapper

import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.dto.AccountDTO
import money.tegro.market.core.model.AccountModel
import money.tegro.market.core.toSafeBounceable
import reactor.core.publisher.Mono

@Singleton
class AccountMapper {
    fun map(it: AccountModel): Mono<AccountDTO> = mono {
        AccountDTO(
            address = it.address.toSafeBounceable(),
        )
    }
}
