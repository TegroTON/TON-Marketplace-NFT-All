package money.tegro.market.db

import java.util.stream.Stream
import javax.persistence.EntityManager
import javax.persistence.NoResultException
import javax.persistence.PersistenceContext
import javax.persistence.TypedQuery
import javax.persistence.criteria.*
import kotlin.reflect.KProperty1

class KotlinCriteriaQuery<T>(
    protected val builder: CriteriaBuilder,
    protected val query: CriteriaQuery<T>
) : CriteriaQuery<T> by query {

    private val wheres = mutableListOf<Predicate>()
    private var and: Boolean = true

    fun addWhere(predicate: Predicate?, addWhen: Boolean = true): KotlinCriteriaQuery<T> {
        if (predicate != null && addWhen)
            wheres.add(predicate)
        return this
    }

    fun or(): KotlinCriteriaQuery<T> {
        and = false
        return this
    }

    fun finalizeWhere(): KotlinCriteriaQuery<T> {
        if (wheres.isNotEmpty())
            where(if (and) builder.and(*wheres.toTypedArray()); else builder.or(*wheres.toTypedArray()))
        return this
    }
}

typealias CriteriaQueryBuilder<V, T> = (CriteriaBuilder.(query: KotlinCriteriaQuery<V>, entity: Root<T>) -> Unit)?

abstract class BaseRepository<T> {
    @PersistenceContext
    protected lateinit var entityManager: EntityManager

    abstract val resourceClass: Class<T>

    protected fun <V> criteria(
        clazz: Class<V>,
        build: CriteriaQueryBuilder<V, T> = null
    ): TypedQuery<V> {
        val builder: CriteriaBuilder = entityManager.criteriaBuilder
        val originalQuery: CriteriaQuery<V> = builder.createQuery(clazz)
        val query: KotlinCriteriaQuery<V> = KotlinCriteriaQuery(builder, originalQuery)
        val entity: Root<T> = query.from(resourceClass)
        build?.invoke(builder, query, entity)
        query.finalizeWhere()
        return entityManager.createQuery(originalQuery)
    }

    protected fun criteria(build: CriteriaQueryBuilder<T, T> = null) =
        criteria(resourceClass)
}

fun TypedQuery<java.lang.Long>.longList(): List<Long> = resultList.map { it.toLong() }
fun TypedQuery<java.lang.Long>.longStream(): Stream<Long> = resultStream.map { it.toLong() }
fun TypedQuery<java.lang.Long>.longResult(): Long? = singleResult?.toLong()

fun <T> TypedQuery<T>.findOne(): T? {
    return try {
        this.singleResult
    } catch (e: NoResultException) {
        null
    }
}

fun <T, V> Root<T>.get(prop: KProperty1<T, V>): Path<V> = this.get(prop.name)
