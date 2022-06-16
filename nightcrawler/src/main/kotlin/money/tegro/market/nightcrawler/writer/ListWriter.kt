package money.tegro.market.nightcrawler.writer

import org.springframework.batch.item.ItemWriter

class ListWriter<T>(private val underlyingWriter: ItemWriter<T>) : ItemWriter<List<T>> {
    override fun write(items: MutableList<out List<T>>) {
        items.forEach {
            underlyingWriter.write(it)
        }
    }
}
