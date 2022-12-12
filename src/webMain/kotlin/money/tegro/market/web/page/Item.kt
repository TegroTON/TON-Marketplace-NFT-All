package money.tegro.market.web.page

import dev.fritz2.core.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import money.tegro.market.model.ItemModel
import money.tegro.market.model.OrdinaryItemModel
import money.tegro.market.model.SaleItemModel
import money.tegro.market.resource.ItemResource
import money.tegro.market.web.component.Button
import money.tegro.market.web.component.Link
import money.tegro.market.web.formatTON
import money.tegro.market.web.modal.BuyModal
import money.tegro.market.web.modal.CancelSaleModal
import money.tegro.market.web.modal.SellModal
import money.tegro.market.web.modal.TransferModal
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.normalizeAddress
import money.tegro.market.web.normalizeAndShorten
import money.tegro.market.web.store.GlobalConnectionStore
import money.tegro.market.web.store.PopOverStore
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance


fun RenderContext.Item(address: String) {
    val itemStore = object : RootStore<ItemModel?>(null) {
        private val httpClient: HttpClient by DI.global.instance()
        val load = handle { _ ->
            httpClient.get(ItemResource.ByAddress(address = address))
                .body<ItemModel>()
        }

        init {
            load()
        }
    }

    main("mx-3 lg:mx-6") {
        section("container relative pt-12 mx-auto flex flex-col gap-12 justify-center") {
            nav {
                ol("flex flex-wrap text-gray-500 gap-2") {
                    li {
                        Link(setOf("explore")) {
                            +"Explore"
                        }
                    }

                    li {
                        +"/"
                    }

                    Link(
                        itemStore.data
                            .map {
                                it?.collection?.address?.let { addr -> setOf("collection", addr) } ?: setOf("explore")
                            }
                    ) {
                        itemStore.data.map { it?.collection?.name ?: "Standalone" }.renderText(this)
                    }
                    li {
                        +"/"
                    }
                    li {
                        itemStore.data
                            .map { it?.name ?: "Item" }
                            .renderText(this)
                    }
                }
            }

            div("relative grid gap-12 grid-cols-1 md:grid-cols-3") {
                div {
                    img("sticky top-36 w-full h-auto object-cover rounded-lg") {
                        itemStore.data
                            .map { it?.image?.original ?: "./assets/img/user-1.svg" }.let(::src)
                        itemStore.data
                            .map { it?.name ?: "Item Image" }.let(::alt)
                    }
                }

                div("md:col-span-2 flex flex-col gap-8") {
                    div("flex gap-2") {
                        itemStore.data
                            .filterNotNull()
                            .render(this) { item ->
                                when (item) {
                                    is SaleItemModel ->
                                        div("px-6 py-3 text-green bg-green-soft rounded-2xl uppercase") {
                                            +"For Sale"
                                        }

                                    is OrdinaryItemModel ->
                                        div("px-6 py-3 text-white bg-soft rounded-2xl uppercase") {
                                            +"Not For Sale"
                                        }
                                }
                            }
                    }

                    div("flex flex-col gap-4 px-4") {
                        h1("text-3xl font-raleway font-medium") {
                            itemStore.data
                                .map { it?.name ?: "..." }
                                .renderText(this)
                        }

                        p("text-gray-500") {
                            itemStore.data
                                .map { it?.description ?: "..." }
                                .renderText(this)
                        }
                    }


                    itemStore.data
                        .mapNotNull { it as? SaleItemModel }
                        .render { item ->
                            div("flex flex-col gap-4 rounded-lg bg-soft px-6 py-4") {
                                div("flex uppercase items-center") {
                                    span("flex-grow font-raleway text-xl") {
                                        +"Price"
                                    }
                                    span("text-3xl") {
                                        +item.fullPrice.formatTON().plus(" TON")
                                    }
                                }
                                div("flex text-gray-500") {
                                    span("flex-grow") {
                                        +"Plus a network fee of"
                                    }
                                    span {
                                        +item.networkFee.formatTON().plus(" TON")
                                    }
                                }
                            }
                        }

                    div("grid grid-cols-1 lg:grid-cols-2 gap-4") {
                        val connectionStore: GlobalConnectionStore by DI.global.instance()
                        val popOverStore: PopOverStore by DI.global.instance()
                        itemStore.data
                            .filterNotNull()
                            .combine(connectionStore.address) { a, b -> a to b }
                            .render(this) { (item, address) ->
                                when (item) {
                                    is SaleItemModel -> {
                                        if (address?.let(::normalizeAddress) == item.owner?.let(::normalizeAddress)) { // Item is owned by the user
                                            Button(ButtonKind.SECONDARY, "lg:col-span-2") {
                                                clicks handledBy popOverStore.cancelSale
                                                +"Cancel Sale"
                                            }
                                        } else {
                                            Button(ButtonKind.PRIMARY, "lg:col-span-2") {
                                                clicks handledBy popOverStore.buy
                                                +"Buy Item"
                                            }
                                        }
                                    }

                                    is OrdinaryItemModel -> {
                                        if (address?.let(::normalizeAddress) == item.owner?.let(::normalizeAddress)) { // Item is owned by the user
                                            Button(ButtonKind.PRIMARY) {
                                                clicks handledBy popOverStore.sell
                                                +"Put On Sale"
                                            }
                                            Button(ButtonKind.SECONDARY) {
                                                clicks handledBy popOverStore.transfer
                                                +"Transfer Ownership"
                                            }
                                        }
                                    }
                                }
                            }
                    }

                    div("grid grid-cols-1 lg:grid-cols-2 gap-4") {
                        Link(
                            itemStore.data
                                .map { setOf("profile", it?.owner.orEmpty()) },
                            "rounded-lg bg-soft px-6 py-4 flex-grow flex flex-col gap-2"
                        ) {
                            h4("font-raleway text-sm text-gray-500") {
                                +"Owner"
                            }

                            div("flex items-center gap-2") {
                                img("w-10 h-10 rounded-full") {
                                    src("./assets/img/user-1.svg")
                                    alt("Profile Image")
                                }

                                h4("flex-grow text-lg") {
                                    itemStore.data
                                        .map { it?.owner?.let(::normalizeAndShorten) ?: "..." }
                                        .renderText(this)
                                }

                                i("fa-solid fa-angle-right") { }
                            }
                        }

                        Link(
                            itemStore.data
                                .map {
                                    it?.collection?.let { collection -> setOf("collection", collection.address) }
                                        ?: setOf("explore")
                                },
                            "rounded-lg bg-soft px-6 py-4 flex-grow flex flex-col gap-2"
                        ) {
                            h4("font-raleway text-sm text-gray-500") {
                                +"Collection"
                            }

                            div("flex items-center gap-2") {
                                img("w-10 h-10 rounded-full") {
                                    itemStore.data
                                        .map {
                                            it?.collection?.image?.original ?: "./assets/img/user-1.svg"
                                        }.let(::src)
                                    itemStore.data
                                        .map { it?.collection?.name ?: "Collection Image" }.let(::alt)

                                }

                                h4("flex-grow text-lg") {
                                    itemStore.data
                                        .map { it?.collection?.name ?: "No Collection" }
                                        .renderText(this)
                                }

                                i("fa-solid fa-angle-right") {}
                            }
                        }
                    }

                    div("rounded-lg bg-soft px-6 py-4 flex flex-col gap-2") {
                        h4("text-lg font-raleway") {
                            +"Details"
                        }

                        ul("flex flex-col gap-2") {
                            li {
                                a("p-4 flex gap-2 items-center rounded-lg border border-gray-900") {
                                    target("_blank")
                                    itemStore.data
                                        .filterNotNull()
                                        .map { "https://testnet.tonscan.org/address/${it.address}" }.let(::href)

                                    span("text-gray-500") {
                                        +"Contract Address"
                                    }

                                    span("flex-grow text-right") {
                                        itemStore.data
                                            .filterNotNull()
                                            .map { normalizeAndShorten(it.address) }
                                            .renderText(this)
                                    }

                                    i("fa-solid fa-angle-right") {}
                                }
                            }

                            li("p-4 flex gap-2 items-center rounded-lg border border-gray-900") {
                                span("text-gray-500") {
                                    +"Index"
                                }

                                span("flex-grow text-right") {
                                    itemStore.data
                                        .filterNotNull()
                                        .map { it.index }
                                        .renderText(this)
                                }
                            }

                            li {
                                a("p-4 flex gap-2 items-center rounded-lg border border-gray-900") {
                                    target("_blank")
                                    itemStore.data
                                        .mapNotNull { it?.owner?.let { owner -> "https://testnet.tonscan.org/address/$owner}" } }
                                        .let(::href)

                                    span("text-gray-500") {
                                        +"Owner Address"
                                    }

                                    span("flex-grow text-right") {
                                        itemStore.data
                                            .map { it?.owner?.let(::normalizeAndShorten) ?: "N/A" }
                                            .renderText(this)
                                    }

                                    i("fa-solid fa-angle-right") {}
                                }
                            }
                        }
                    }
                }
            }

            div("rounded-lg bg-soft px-6 py-4 flex-col gap-4") { // Attributes
                itemStore.data.map { if (it?.attributes.isNullOrEmpty()) "hidden" else "flex" }.let(::className)

                h4("text-lg font-raleway") {
                    +"Attributes"
                }

                ul("grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2") {
                    itemStore.data
                        .map { it?.attributes.orEmpty().toList() }
                        .renderEach(into = this) { (trait, value) ->
                            li {
                                a("p-4 flex gap-2 items-center rounded-lg border border-gray-900") {
                                    span("text-gray-500") {
                                        +trait
                                    }

                                    span("flex-grow text-right") {
                                        +value
                                    }
                                }
                            }
                        }
                }
            }
        }
    }

    itemStore.data
        .filterNotNull()
        .render { item ->
            when (item) {
                is SaleItemModel -> {
                    BuyModal(item)
                    CancelSaleModal(item)
                }

                is OrdinaryItemModel -> {
                    TransferModal(item)
                    SellModal(item)
                }
            }
        }
}
