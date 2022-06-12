package money.tegro.market.nightcrawler

import org.springframework.batch.item.ItemReader
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder
import org.springframework.core.io.Resource

class ResourceLineReader(name: String, resource: Resource) : ItemReader<String> {
    private val underlying = FlatFileItemReaderBuilder<String>()
        .name(name)
        .resource(resource)
        .delimited()
        .names("item")
        .fieldSetMapper {
            it.values.first()
        }
        .build()

    override fun read() = underlying.read()
}
