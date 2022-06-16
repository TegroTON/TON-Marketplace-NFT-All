package money.tegro.market.nightcrawler.writer

import org.springframework.batch.integration.async.AsyncItemWriter

class AsyncListWriter<T>(underlying: ListWriter<T>) : AsyncItemWriter<List<T>>() {
    init {
        setDelegate(underlying)
    }
}

