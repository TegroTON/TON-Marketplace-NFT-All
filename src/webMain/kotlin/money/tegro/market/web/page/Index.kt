package money.tegro.market.web.page

import dev.fritz2.core.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import money.tegro.market.model.CollectionModel
import money.tegro.market.resource.AllCollectionsResource
import money.tegro.market.web.client
import money.tegro.market.web.component.Button
import money.tegro.market.web.component.Link
import money.tegro.market.web.model.ButtonKind

fun RenderContext.Index() {
    main("mx-3 lg:mx-6") {
        section("px-0 py-12") {
            div("container relative overflow-hidden rounded-3xl mx-auto") {
                val slide = storeOf(0)

                div("absolute bottom-0 left-0 right-0 p-0 mb-4 flex justify-center") {
                    button("z-10 w-3 h-3 m-1 rounded-full flex-initial") {
                        slide.data
                            .map { if (it == 0) "bg-yellow" else "bg-gray-500" }.let(::className)

                        clicks.map { 0 } handledBy slide.update
                    }

                    button("z-10 w-3 h-3 m-1 rounded-full flex-initial") {
                        slide.data
                            .map { if (it == 1) "bg-yellow" else "bg-gray-500" }.let(::className)

                        clicks.map { 1 } handledBy slide.update
                    }
                }

                div("relative overflow-hidden") {
                    div("relative px-6 pt-48 pb-12") {
                        slide.data
                            .map { if (it == 0) "block" else "hidden" }.let(::className)

                        div("flex flex-wrap items-center lg:px-20 lg:py-16") {
                            div("w-full text-center lg:text-start") {
                                h1("text-5xl font-bold font-raleway mb-6") {
                                    +"Witness the birth of Libermall"
                                }

                                div("text-lg mb-15") {
                                    +"A new, modern and slick NFT marketplace is emerging on the open network"
                                }
                            }
                        }
                        picture {
                            img("absolute -z-10 top-0 right-0 w-full h-full object-cover opacity-50") {
                                alt("Libermall - NFT Marketplace")
                                src("./assets/img/hero-image-2.jpg")
                            }
                        }
                    }

                    div("relative px-6 pt-48 pb-12") {
                        slide.data
                            .map { if (it == 1) "block" else "hidden" }.let(::className)

                        div("flex flex-wrap items-center lg:px-20 lg:py-16") {
                            div("w-full text-center lg:text-start") {
                                h1("text-5xl font-bold font-raleway mb-6") {
                                    +"Witness the birth of Libermall"
                                }

                                div("text-lg mb-15") {
                                    +"A new, modern and slick NFT marketplace is emerging on the open network"
                                }
                            }
                        }
                        picture {
                            img("absolute -z-10 top-0 right-0 w-full h-full object-cover opacity-50") {
                                alt("Libermall - NFT Marketplace")
                                src("./assets/img/hero-image.jpg")
                            }
                        }
                    }
                }
            }
        }

        section("px-0 py-12 pt-28") {
            div("container relative text-center mx-auto") {
                h2("text-5xl font-raleway font-bold mb-4") {
                    +"Discover, Collect and sell "
                    span("text-yellow") {
                        +"extraordinary NFTs"
                    }
                }
                p("mb-12 text-gray-500 text-2xl font-light") {
                    +"Libermall is an emerging NFT marketplace"
                }

                div("flex flex-wrap items-center justify-center") {
                    Link(setOf("explore")) {
                        Button(
                            ButtonKind.PRIMARY,
                            "text-dark-900 bg-yellow px-8 py-4 m-0 border-0 rounded-lg uppercase font-medium text-sm flex flex-nowrap items-center tracking-widest hover:bg-yellow-hover hover:border-yellow-hover",
                        ) {
                            +"Explore"
                        }
                    }
                }
            }
        }

        section("px-0 py-12") {
            div("relative container px-10 py-10 mx-auto overflow-hidden rounded-lg bg-gradient-to-r from-yellow-gradient-start to-yellow-gradient-end") {
                span("block absolute top-0 w-full h-full right-[-70%] md:right-[-80%] xl:right-[-45%]  2xl:right-[-40%] bg-left-top bg-cover ") {
                }

                h2("text-dark-900 text-3xl font-raleway mb-4") {
                    +"Early Access "
                    span("block md:inline") {
                        +"on Libermall"
                    }
                }

                div("mt-10 flex items-center") {
                    Link(setOf("explore")) {
                        Button(kind = ButtonKind.PRIMARY) {
                            +"Explore"
                        }
                    }

                    div("hidden md:flex gap-3 ml-12") {
                        mapOf(
                            "fa-telegram" to "https://t.me/LiberMall",
                            "fa-twitter" to "https://twitter.com/LiberMallNFT",
                            "fa-github" to "https://github.com/LiberMall",
                            "fa-instagram" to "https://www.instagram.com/libermallua",
                            "fa-medium" to "https://libermall.medium.com",
                            "fa-vk" to "https://vk.com/libermall",
                        )
                            .forEach { (icon, link) ->
                                a {
                                    href(link)
                                    target("_blank")
                                    Button(
                                        ButtonKind.SECONDARY,
                                        "p-0 w-12 h-12 hover:bg-dark-900 focus:bg-dark-900 border-dark-900"
                                    ) {
                                        i("fa-brands $icon") { }
                                    }
                                }
                            }
                    }
                }
            }
        }

        section("px-0 py-12") {
            div("container relative mx-auto") {
                div("block md:flex mb-14") {
                    h2("text-4xl font-bold font-raleway mb-0") {
                        +"Top "
                        span("text-yellow") {
                            +"collections"
                        }
                    }

                    div("mt-6 md:mt-0 ml-auto") {
                        Button(kind = ButtonKind.SOFT) {
                            +"All Time"
                        }
                    }
                }

                div("pt-4 flex flex-wrap gap-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3") {
                    val topCollectionsStore = object : RootStore<List<CollectionModel>?>(null) {
                        val load = handle { _ ->
                            client.get(AllCollectionsResource(sort = AllCollectionsResource.Sort.TOP))
                                .body<List<CollectionModel>>()
                        }

                        init {
                            load()
                        }
                    }

                    topCollectionsStore.data
                        .filterNotNull()
                        .map { it.withIndex().toList() }
                        .renderEach(into = this) { (index, collection) ->
                            Link(
                                setOf("collection", collection.address),
                                "flex flex-col lg:flex-row gap-4 rounded-xl p-4 items-center bg-dark-700 hover:bg-gray-900"
                            ) {
                                title(collection.name)

                                span("font-bold") { +"${index + 1}" }

                                picture {
                                    img("rounded-full w-16 h-16") {
                                        alt(collection.name)
                                        src(collection.image.original ?: "./assets/img/user-1.svg")
                                    }
                                }

                                h4("text-lg font-raleway") {
                                    +collection.name
                                }
                            }
                        }
                }

                div("mt-12 flex flex-wrap items-center justify-center") {
                    Link(setOf("explore")) {
                        Button(kind = ButtonKind.PRIMARY) {
                            +"Explore"
                        }
                    }
                }
            }
        }
    }
}
