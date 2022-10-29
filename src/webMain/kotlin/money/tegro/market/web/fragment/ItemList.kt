package money.tegro.market.web.fragment

import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import dev.fritz2.core.storeOf
import dev.fritz2.tracking.tracker
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import money.tegro.market.model.ItemModel
import money.tegro.market.resource.AllItemsResource
import money.tegro.market.web.card.ItemCard
import money.tegro.market.web.component.Button
import money.tegro.market.web.model.ButtonKind
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance

fun RenderContext.ItemList(
    relatedTo: String?, relation: AllItemsResource.Relation?,
    sortStore: RootStore<AllItemsResource.Sort>,
    filterStore: RootStore<AllItemsResource.Filter?>,
) {
    val itemsLoaded = storeOf(0)
    val itemsStore = object : RootStore<List<ItemModel>?>(null) {
        private val httpClient: HttpClient by DI.global.instance()
        val tracking = tracker()

        val load = handle { last ->
            tracking.track {
                last.orEmpty().plus(
                    httpClient.get(
                        AllItemsResource(
                            relatedTo = relatedTo,
                            relation = relation,
                            sort = sortStore.current,
                            filter = filterStore.current,
                            drop = itemsLoaded.current,
                            take = 16
                        )
                    )
                        .body<List<ItemModel>>()
                )
                    .also { itemsLoaded.update(it.size) }
            }
        }
    }

    sortStore.data.combine(filterStore.data) { _, _ -> } handledBy {
        // Reload items when sort or filters change
        itemsLoaded.update(0)
        itemsStore.update(null)
        itemsStore.load()
    }

    div("grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4") { // Collection Body
        itemsStore.data
            .filterNotNull()
            .renderEach(into = this) { ItemCard(it) }
    }

    itemsStore.tracking.data.render {
        if (it)
            i("fa-regular fa-spinner animate-spin text-3xl text-yellow text-center") {}
        else
            Button(ButtonKind.SECONDARY) {
                clicks handledBy itemsStore.load

                +"Load More"
            }
    }
}
