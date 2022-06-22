package money.tegro.market.nightcrawler

import jakarta.inject.Singleton
import money.tegro.market.core.model.AttributeModel
import money.tegro.market.core.repository.AttributeRepository
import java.util.function.Consumer

@Singleton
class AttributeWriter(
    private val repository: AttributeRepository
) : Consumer<AttributeModel> {
    override fun accept(it: AttributeModel) {
        repository.findByItemAndTrait(it.item, it.trait).block()?.let { attribute ->
            val id = attribute.id
            requireNotNull(id)
            repository.update(id, it.value)
        } ?: run {
            repository.save(it).subscribe()
        }
    }
}
