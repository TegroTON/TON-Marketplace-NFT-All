package money.tegro.market.nightcrawler

import org.springframework.batch.item.database.JpaPagingItemReader
import javax.persistence.EntityManagerFactory

abstract class EntityReader<T> : JpaPagingItemReader<T>() {
    companion object {
        inline fun <reified T> configure(it: EntityReader<T>, emf: EntityManagerFactory) {
            it.setName("${T::class.java.simpleName}Reader")
            it.setQueryString("from ${T::class.java.simpleName}")
            it.setEntityManagerFactory(emf)
            it.isSaveState = false
        }
    }
}
