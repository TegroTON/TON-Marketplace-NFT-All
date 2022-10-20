package money.tegro.market.web.page

import dev.fritz2.core.*
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
import money.tegro.market.web.client
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
import money.tegro.market.web.store.ConnectionStore
import money.tegro.market.web.store.PopOverStore


fun RenderContext.Item(address: String) {
    val itemStore = object : RootStore<ItemModel?>(null) {
        val load = handle { _ ->
            client.get(ItemResource.ByAddress(address = address))
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
                    itemStore.data
                        .map { it?.collection }
                        .filterNotNull()
                        .render { collection ->
                            li {
                                span {
                                    +"/"
                                }
                            }
                            li {
                                Link(setOf("collection", collection.address)) {
                                    +collection.name
                                }
                            }
                        }
                }
                li {
                    span {
                        +"/"
                    }
                }
                li {
                    span {
                        itemStore.data
                            .map { it?.name ?: "Item" }
                            .renderText()
                    }
                }
            }
        }

        div("grid gap-12 grid-cols-1 lg:grid-cols-3") {
            div {
                img("w-full h-auto object-cover rounded-2xl") {
                    itemStore.data
                        .map { it?.image?.original ?: "./assets/img/user-1.svg" } handledBy ::src
                    itemStore.data
                        .map { it?.name ?: "Item Image" } handledBy ::alt
                }
            }

            div("lg:col-span-2 flex flex-col gap-8") {
                div("flex gap-2") {
                    itemStore.data
                        .filterNotNull()
                        .render { item ->
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
                            .renderText()
                    }

                    p("text-gray-500") {
                        itemStore.data
                            .map { it?.description ?: "..." }
                            .renderText()
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
                    itemStore.data
                        .filterNotNull()
                        .combine(ConnectionStore.data) { a, b -> a to b }
                        .render { (item, connection) ->
                            when (item) {
                                is SaleItemModel -> {
                                    if (connection?.walletAddress?.let(::normalizeAddress) == item.owner?.let(::normalizeAddress)) { // Item is owned by the user
                                        Button(ButtonKind.SECONDARY, "lg:col-span-2") {
                                            clicks handledBy PopOverStore.cancelSale
                                            +"Cancel Sale"
                                        }
                                    } else {
                                        Button(ButtonKind.PRIMARY, "lg:col-span-2") {
                                            clicks handledBy PopOverStore.buy
                                            +"Buy Item"
                                        }
                                    }
                                }

                                is OrdinaryItemModel -> {
                                    if (connection?.walletAddress?.let(::normalizeAddress) == item.owner?.let(::normalizeAddress)) { // Item is owned by the user
                                        Button(ButtonKind.PRIMARY) {
                                            clicks handledBy PopOverStore.sell
                                            +"Put On Sale"
                                        }
                                        Button(ButtonKind.SECONDARY) {
                                            clicks handledBy PopOverStore.transfer
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
                                    .renderText()
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
                                    } handledBy ::src
                                itemStore.data
                                    .map { it?.collection?.name ?: "Collection Image" } handledBy ::alt

                            }

                            h4("flex-grow text-lg") {
                                itemStore.data
                                    .map { it?.collection?.name ?: "No Collection" }
                                    .renderText()
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
                                    .map { "https://testnet.tonscan.org/address/${it.address}" } handledBy ::href

                                span("text-gray-500") {
                                    +"Contract Address"
                                }

                                span("flex-grow text-right") {
                                    itemStore.data
                                        .filterNotNull()
                                        .map { normalizeAddress(it.address) }
                                        .renderText()
                                }

                                i("fa-solid fa-angle-right") {}
                            }
                        }

                        itemStore.data
                            .mapNotNull { it as? SaleItemModel }
                            .render { item ->
                                li {
                                    a("p-4 flex gap-2 items-center rounded-lg border border-gray-900") {
                                        target("_blank")
                                        href("https://testnet.tonscan.org/address/${item.sale}")

                                        span("text-gray-500") {
                                            +"Sale Address"
                                        }

                                        span("flex-grow text-right") {
                                            +normalizeAndShorten(item.sale)
                                        }

                                        i("fa-solid fa-angle-right") { }
                                    }
                                }
                            }
                    }
                }
            }
        }

        itemStore.data
            .mapNotNull { it?.attributes }
            .render { attributes ->
                div("rounded-lg bg-soft px-6 py-4 flex flex-col gap-4") { // Attributes
                    h4("text-lg font-raleway") {
                        +"Attributes"
                    }

                    ul("grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2") {
                        for ((trait, value) in attributes) {
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
