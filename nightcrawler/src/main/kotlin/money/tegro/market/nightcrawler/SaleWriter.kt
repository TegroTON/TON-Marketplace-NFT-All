package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import money.tegro.market.core.model.SaleModel
import money.tegro.market.core.repository.SaleRepository
import java.util.function.Consumer

@Singleton
class SaleWriter(
    private val repository: SaleRepository
) : Consumer<SaleModel> {
    override fun accept(it: SaleModel) {
        repository.findById(it.address).block()?.let { sale ->
            repository.update(it).subscribe()
        } ?: run {
            repository.save(it).subscribe()
        }
    }
}