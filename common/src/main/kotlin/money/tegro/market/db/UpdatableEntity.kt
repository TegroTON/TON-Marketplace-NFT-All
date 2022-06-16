package money.tegro.market.db

import java.time.Instant
import kotlin.reflect.KMutableProperty

interface UpdatableEntity {
    val discovered: Instant
    var updated: Instant?
    var modified: Instant?

    // Change if Not Equal: If property value != supplied value, it is set to that value and modified is set to current time
    // TODO: propertly handle arrays
    fun <T> cneq(property: KMutableProperty<T>, value: T) {
        if (property != value) {
            property.setter.call(value)
            modified = Instant.now()
        }
    }
}
