package money.tegro.market.web.page

import dev.fritz2.core.RenderContext
import dev.fritz2.core.RootStore
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.flow.filterNotNull
import money.tegro.market.model.CollectionModel
import money.tegro.market.resource.AllCollectionsResource
import money.tegro.market.web.card.CollectionCard
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance

fun RenderContext.Explore() {
    main("mx-3 lg:mx-6") {
        section("px-0 py-12") {
            div("container mx-auto") {
                h1("font-raleway text-4xl") {
                    +"Explore collections"
                }
            }

            div("grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4") {
                val collectionsStore = object : RootStore<List<CollectionModel>?>(null) {
                    private val httpClient: HttpClient by DI.global.instance()
                    val load = handle { _ ->
                        httpClient.get(AllCollectionsResource(sort = AllCollectionsResource.Sort.ALL))
                            .body<List<CollectionModel>>()
                    }

                    init {
                        load()
                    }
                }

                collectionsStore.data
                    .filterNotNull()
                    .renderEach(into = this) { CollectionCard(it) }
            }
        }
    }
}
