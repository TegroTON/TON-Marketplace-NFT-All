package money.tegro.market.web.page

import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.core.src
import dev.fritz2.core.target
import kotlinx.coroutines.flow.filterNotNull
import money.tegro.market.web.store.CollectionStore
import money.tegro.market.web.store.ItemStore


fun RenderContext.Item(address: String) {
    ItemStore.load(address)

    ItemStore.data.filterNotNull().render { item ->
        main("mx-3 lg:mx-6") {
            section("container relative pt-12 mx-auto flex flex-col gap-12 justify-center") {
                nav {
                    ol("flex flex-wrap text-gray-500 gap-2") {
                        li {
                            a {
                                href("#explore")
                                +"Explore"
                            }
                        }
                        if (item.collection != null) {
                            CollectionStore.data.filterNotNull().render { collection ->
                                li {
                                    span {
                                        +"/"
                                    }
                                }
                                li {
                                    a {
                                        href("#collection/${collection.address}")
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
                                +item.name
                            }
                        }
                    }
                }

                div("grid gap-12 grid-cols-1 lg:grid-cols-3") {
                    div {
                        img("w-full h-auto object-cover rounded-2xl") {
                            src(item.image.original ?: "./assets/img/user-1.svg")
                        }
                    }

                    div("lg:col-span-2 flex flex-col gap-8") {
                        div("flex gap-2") {
                            if (item.sale != null) {
                                div("px-6 py-3 text-green bg-green-soft rounded-2xl uppercase") {
                                    +"For Sale"
                                }
                            } else {
                                div("px-6 py-3 text-white bg-soft rounded-2xl uppercase") {
                                    +"Not For Sale"
                                }
                            }
                        }

                        div("flex flex-col gap-4 px-4") {
                            h1("text-3xl font-raleway font-medium") {
                                +item.name
                            }

                            p("text-gray-500") {
                                +item.description
                            }
                        }

                        // Actions here

                        div("grid grid-cols-1 lg:grid-cols-2 gap-4") {
                            a("rounded-lg bg-soft px-6 py-4 flex-grow flex flex-col gap-2") {
                                href("/profile/${item.owner}")

                                h4("font-raleway text-sm text-gray-500") {
                                    +"Owner"
                                }

                                div("flex items-center gap-2") {
                                    img("w-10 h-10 rounded-full") {
                                        src("./assets/img/user-1.svg")
                                    }

                                    h4("flex-grow text-lg") {
                                        +(item.owner?.take(12)?.plus("...") ?: "Owner")
                                    }

                                    i("fa-solid fa-angle-right") { }
                                }
                            }

                            a("rounded-lg bg-soft px-6 py-4 flex-grow flex flex-col gap-2") {
                                href(item.collection?.let { "/collection/$it" } ?: "#explore")

                                h4("font-raleway text-sm text-gray-500") {
                                    +"Collection"
                                }

                                if (item.collection != null) {
                                    CollectionStore.data.filterNotNull().render { collection ->
                                        div("flex items-center gap-2") {
                                            img("w-10 h-10 rounded-full") {
                                                src(collection.image.original ?: "./assets/img/user-1.svg")
                                            }

                                            h4("flex-grow text-lg") {
                                                +collection.name
                                            }

                                            i("fa-solid fa-angle-right") {}
                                        }
                                    }
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
                                        href("https://testnet.tonscan.org/address/${item.address}")

                                        span("text-gray-500") {
                                            +"Contract Address"
                                        }

                                        span("flex-grow text-right") {
                                            +(item.address.take(12).plus("..."))
                                        }

                                        i("fa-solid fa-angle-right") {}
                                    }
                                }

                                if (item.sale != null) {
                                    li {
                                        a("p-4 flex gap-2 items-center rounded-lg border border-gray-900") {
                                            target("_blank")
                                            href("https://testnet.tonscan.org/address/${item.sale}")

                                            span("text-gray-500") {
                                                +"Sale Address"
                                            }

                                            span("flex-grow text-right") {
                                                +item.sale.take(12).plus("...")
                                            }

                                            i("fa-solid fa-angle-right") { }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (item.attributes.isNotEmpty()) {
                    div("rounded-lg bg-soft px-6 py-4 flex flex-col gap-4") { // Attributes
                        h4("text-lg font-raleway") {
                            +"Attributes"
                        }

                        ul("grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2") {
                            for ((trait, value) in item.attributes) {
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
        }
    }
}
