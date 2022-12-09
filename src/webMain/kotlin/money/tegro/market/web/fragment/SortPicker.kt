package money.tegro.market.web.fragment

import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.value
import dev.fritz2.core.values
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import money.tegro.market.resource.ItemResource

fun RenderContext.SortPicker(sortStore: RootStore<ItemResource.All.Sort>) =
    select("px-6 py-3 border border-border-soft rounded-lg bg-dark-700 text-white") {
        changes.values()
            .map { Json.decodeFromString<ItemResource.All.Sort>(it) } handledBy sortStore.update

        ItemResource.All.Sort.values()
            .mapIndexed { index, sort ->
                option() {
                    value(Json.encodeToString(sort))
                    +sort.toString().lowercase().replaceFirstChar { it.uppercase() }
                        .replace("_up", " - Low to High")
                        .replace("_down", " - High to Low")
                }
            }
    }
