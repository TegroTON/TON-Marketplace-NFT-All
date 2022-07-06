package money.tegro.market.nightcrawler.process

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.blockchain.referenceBlock
import money.tegro.market.core.dto.toSafeBounceable
import money.tegro.market.core.model.AttributeModel
import money.tegro.market.core.model.ItemModel
import money.tegro.market.core.repository.AttributeRepository
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.value
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.lite.api.LiteApi
import reactor.kotlin.core.publisher.toFlux

@Prototype
class ItemProcess(
    private val liteApi: LiteApi,
    private val attributeRepository: AttributeRepository
) {
    operator fun invoke(referenceBlock: suspend () -> TonNodeBlockIdExt = liteApi.referenceBlock()) =
        { it: ItemModel ->
            mono {
                val item = NFTItem.of(it.address.to(), liteApi, referenceBlock)
                val metadata = item.metadata(liteApi, referenceBlock)

                metadata.attributes.orEmpty().toFlux()
                    .map { attribute -> AttributeModel(it.address, attribute) }
                    .subscribe {
                        attributeRepository.upsert(it).subscribe()
                    }

                it.copy(item)?.copy(metadata)
                    ?: it.apply {
                        logger.warn(
                            "couldn't update item {}, something went wrong",
                            value("address", address.toSafeBounceable())
                        )
                    }
            }
        }

    companion object : KLogging()
}
