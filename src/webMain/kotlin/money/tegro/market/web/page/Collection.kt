package money.tegro.market.web.page

import dev.fritz2.core.*
import dev.fritz2.tracking.tracker
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import money.tegro.market.model.CollectionModel
import money.tegro.market.model.ItemModel
import money.tegro.market.resource.CollectionResource
import money.tegro.market.resource.ItemResource
import money.tegro.market.web.card.ItemCard
import money.tegro.market.web.client
import money.tegro.market.web.component.Button
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.normalizeAndShorten

fun RenderContext.Collection(address: String) {
    val collectionStore = object : RootStore<CollectionModel?>(null) {
        val load = handle { _ ->
            client.get(CollectionResource.ByAddress(address = address))
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
                    .map { it?.coverImage?.original ?: "./assets/img/profile-hero.jpg" } handledBy ::src
                collectionStore.data
                    .map { it?.name ?: "Collection Cover Image" } handledBy ::alt
            }
        }
    }

    main("mx-3 lg:mx-6") {
        section("container relative px-3 mx-auto gap-8 grid grid-cols-1 lg:grid-cols-3 xl:grid-cols-4") {
            div {// Left panel
                div("relative top-0 overflow-hidden rounded-xl bg-dark-700 bg-white/[.02] backdrop-blur-3xl -mt-24") { // Card
                    div("flex flex-col gap-6 p-6") { // Card body
                        div("flex flex-col items-center") {
                            div { // Image
                                img("w-full h-32 rounded-full object-cover align-middle") {
                                    collectionStore.data
                                        .map { it?.image?.original ?: "./assets/img/user-1.svg" } handledBy ::src
                                    collectionStore.data
                                        .map { it?.name ?: "Collection Image" } handledBy ::alt
                                }
                            }

                            // Main actions here
                        }

                        h1("text-3xl font-raleway") {
                            collectionStore.data
                                .map { it?.name ?: "..." }
                                .renderText()
                        }

                        div("text-gray-500") { // Collection description
                            p {
                                collectionStore.data
                                    .map { it?.description.orEmpty() }
                                    .renderText()
                            }
                        }
                    }

                    div("text-center px-12 py-4") { // Card footer
                        +"Created by "
                        span("text-yellow") {
                            collectionStore.data
                                .map { it?.owner?.let(::normalizeAndShorten) ?: "..." }
                                .renderText()
                        }
                    }
                }

                // Filters here
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
                                .renderText()
                        }
                    }

                    div("p-6 text-center flex flex-col gap-2 flex-1") {
                        h5("uppercase text-gray-500") {
                            +"Address"
                        }

                        p {
                            collectionStore.data
                                .map { it?.address?.let(::normalizeAndShorten) ?: "..." }
                                .renderText()
                        }
                    }
                }

                val sortReverseStore = storeOf(false)
                val sortStore = storeOf(ItemResource.ByRelation.Sort.INDEX)
                div("flex items-center relative") {
                    ul("overflow-auto flex items-center flex-grow") { // Collection tabs
                        li {
                            Button(ButtonKind.SECONDARY, "rounded-none rounded-t-lg border-0 border-b") {
                                +"Items"
                            }
                        }
                    }

                    val dropdownOpen = storeOf(false)
                    Button(ButtonKind.SOFT, "gap-2") {
                        clicks.map { !dropdownOpen.current } handledBy dropdownOpen.update

                        sortReverseStore.data.render {
                            if (it) {
                                i("fa-regular fa-arrow-down-z-a") {}
                            } else {
                                i("fa-regular fa-arrow-up-a-z") {}
                            }
                        }
                        span {
                            sortStore.data.renderText()
                        }
                    }

                    ul("absolute rounded-lg bg-gray-900 block top-12 right-0 transition ease-in-out delay-150 duration-300") {
                        className(
                            dropdownOpen.data
                                .map { if (it) "visible opacity-100" else "invisible opacity-0" }
                        )

                        ItemResource.ByRelation.Sort.values()
                            .flatMap { listOf(false to it, true to it) }
                            .forEach { (reversed, sort) ->
                                li {
                                    button("px-6 py-3 flex items-center gap-2 hover:bg-dark-700") {
                                        type("button")

                                        clicks.map { reversed } handledBy sortReverseStore.update
                                        clicks.map { sort } handledBy sortStore.update

                                        if (reversed) {
                                            i("fa-regular fa-arrow-down-z-a") {}
                                        } else {
                                            i("fa-regular fa-arrow-up-a-z") {}
                                        }

                                        span {
                                            +sort.toString().lowercase().replaceFirstChar { it.uppercase() }
                                                .plus(if (reversed) " - descending" else " - ascending")
                                        }
                                    }
                                }
                            }
                    }
                }

                val itemsLoaded = storeOf(0)
                val itemsStore = object : RootStore<List<ItemModel>?>(null) {
                    val tracking = tracker()

                    val load = handle { last ->
                        tracking.track {
                            last.orEmpty().plus(
                                client.get(
                                    ItemResource.ByRelation(
                                        address = address,
                                        relation = ItemResource.ByRelation.Relation.COLLECTION,
                                        sortItems = sortStore.current,
                                        sortReverse = sortReverseStore.current,
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

                sortReverseStore.data.combine(sortStore.data) { _, _ -> } handledBy {
                    // Reload items when sort changes
                    itemsLoaded.update(0)
                    itemsStore.update(null)
                    itemsStore.load()
                }


                div("grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4") { // Collection Body
                    itemsStore.data
                        .filterNotNull()
                        .renderEach { ItemCard(it) }
                }

                itemsStore.tracking.data.render {
                    if (it)
                        i("fa-regular fa-spinner animate-spin text-3xl text-yellow text-center") {}
                }

                Button(ButtonKind.SECONDARY) {
                    clicks handledBy itemsStore.load

                    +"Load More"
                }
            }
        }
    }
}
