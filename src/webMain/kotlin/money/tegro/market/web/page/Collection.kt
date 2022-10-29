package money.tegro.market.web.page

import dev.fritz2.core.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.flow.map
import money.tegro.market.model.CollectionModel
import money.tegro.market.resource.AllItemsResource
import money.tegro.market.resource.CollectionResource
import money.tegro.market.web.component.Button
import money.tegro.market.web.fragment.FilterPanel
import money.tegro.market.web.fragment.ItemList
import money.tegro.market.web.fragment.SortPicker
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.normalizeAndShorten
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance

fun RenderContext.Collection(address: String) {
    val collectionStore = object : RootStore<CollectionModel?>(null) {
        private val httpClient: HttpClient by DI.global.instance()
        val load = handle { _ ->
            httpClient.get(CollectionResource(address = address))
                .body<CollectionModel>()
        }

        init {
            load()
        }
    }

    section("min-w-full m-0 -mb-6 bg-gray-900") {
        picture {
            img("w-full h-[340px] object-cover align-middle") {
                collectionStore.data
                    .map { it?.coverImage?.original ?: "./assets/img/profile-hero.jpg" }.let(::src)
                collectionStore.data
                    .map { it?.name ?: "Collection Cover Image" }.let(::alt)
            }
        }
    }

    main("mx-3 lg:mx-6") {
        section("container relative px-3 mx-auto gap-8 grid grid-cols-1 lg:grid-cols-3 xl:grid-cols-4") {
            val filterStore = storeOf<AllItemsResource.Filter?>(null)

            div("flex flex-col gap-8") {// Left panel
                div("relative top-0 overflow-hidden rounded-lg bg-dark-700 bg-white/[.02] backdrop-blur-3xl -mt-24 pb-24") { // Card
                    div("flex flex-col gap-6 p-6") { // Card body
                        div("flex flex-col items-center") {
                            div { // Image
                                img("w-full h-32 rounded-full object-cover align-middle") {
                                    collectionStore.data
                                        .map { it?.image?.original ?: "./assets/img/user-1.svg" }.let(::src)
                                    collectionStore.data
                                        .map { it?.name ?: "Collection Image" }.let(::alt)
                                }
                            }

                            // Main actions here
                        }

                        h1("text-3xl font-raleway") {
                            collectionStore.data
                                .map { it?.name ?: "..." }
                                .renderText(this)
                        }

                        div("text-gray-500") { // Collection description
                            p {
                                collectionStore.data
                                    .map { it?.description.orEmpty() }
                                    .renderText(this)
                            }
                        }
                    }

                    div("text-center px-12 py-4") { // Card footer
                        +"Created by "
                        span("text-yellow") {
                            collectionStore.data
                                .map { it?.owner?.let(::normalizeAndShorten) ?: "..." }
                                .renderText(this)
                        }
                    }
                }

                FilterPanel(filterStore)
            }

            div("lg:col-span-2 xl:col-span-3 flex flex-col gap-6") { // Right panel
                div("overflow-auto rounded-xl bg-dark-700 bg-white/[.02] backdrop-blur-3xl flex items-center justify-between") { // Stats card
                    div("p-6 text-center flex flex-col gap-2 flex-1") {
                        h5("uppercase text-gray-500") {
                            +"Items"
                        }

                        p("uppercase") {
                            collectionStore.data
                                .map { it?.numberOfItems ?: "..." }
                                .renderText(this)
                        }
                    }

                    div("p-6 text-center flex flex-col gap-2 flex-1") {
                        h5("uppercase text-gray-500") {
                            +"Address"
                        }

                        p {
                            collectionStore.data
                                .map { it?.address?.let(::normalizeAndShorten) ?: "..." }
                                .renderText(this)
                        }
                    }
                }

                val sortStore = storeOf(AllItemsResource.Sort.INDEX_UP)
                div("flex items-center relative") {
                    ul("overflow-auto flex items-center flex-grow") { // Collection tabs
                        li {
                            Button(ButtonKind.SECONDARY, "rounded-none rounded-t-lg border-0 border-b") {
                                +"Items"
                            }
                        }
                    }

                    SortPicker(sortStore)
                }

                ItemList(
                    relatedTo = address,
                    relation = AllItemsResource.Relation.COLLECTION,
                    sortStore = sortStore,
                    filterStore = filterStore,
                )
            }
        }
    }
}
