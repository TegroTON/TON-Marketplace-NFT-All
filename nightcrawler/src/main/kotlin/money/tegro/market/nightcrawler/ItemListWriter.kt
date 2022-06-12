package money.tegro.market.nightcrawler

import org.springframework.batch.item.ItemWriter

class ItemListWriter<T>(val underlyingWriter: ItemWriter<T>) : ItemWriter<List<T>> {
    override fun write(items: MutableList<out List<T>>) {
        items.forEach {
            underlyingWriter.write(it)
        }
    }
}
