package money.tegro.market.nightcrawler

import org.springframework.batch.item.database.JpaItemWriter
import javax.persistence.EntityManagerFactory

abstract class EntityWriter<T> : JpaItemWriter<T>() {
    companion object {
        inline fun <reified T> configure(it: EntityWriter<T>, emf: EntityManagerFactory) {
            it.setEntityManagerFactory(emf)
        }
    }
}

