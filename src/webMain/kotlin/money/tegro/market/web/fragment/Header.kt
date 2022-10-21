package money.tegro.market.web.fragment

import dev.fritz2.core.*
import kotlinx.coroutines.flow.map
import money.tegro.market.web.component.Button
import money.tegro.market.web.component.Link
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.model.PopOver
import money.tegro.market.web.store.ConnectionStore
import money.tegro.market.web.store.PopOverStore

fun RenderContext.Header() {
    header("sticky z-30 top-0 px-0 py-6 sm:py-8 bg-dark-900/[.9] backdrop-blur-2xl") {
        div("container relative px-3 mx-auto") {
            nav("relative flex flex-wrap p-0 items-center justify-between") {
                Link(setOf(""), "flex mr-12 items-center") {
                    img("w-12 h-12 object-contain") {
                        alt("Libermall - NFT Marketplace")
                        src("assets/img/logo/apple-icon-57x57.png")
                    }

                    span("font-raleway font-bold text-3xl hidden 2xl:block ml-6") {
                        +"Libermall"
                    }
                }

                Button(ButtonKind.SOFT, "lg:hidden ml-4 order-2") {
                    clicks handledBy PopOverStore.menu

                    i("fa-regular text-xl fa-bars") { }
                }

                div("fixed top-0 left-0 w-3/4 h-screen bg-dark-900 lg:bg-inherit basis-full items-center px-3 py-6 lg:flex lg:basis-auto lg:p-0 lg:w-auto lg:h-auto lg:static grow items-center") {
                    className(
                        PopOverStore.data
                            .map { if (it == PopOver.MENU) "block" else "hidden" }
                    )
                    form("mx-0 lg:mx-12 mb-4 lg:mb-0 order-1 lg:order-2 block grow") {
                        div("border border-solid grow border-border-soft rounded-lg bg-soft relative flex flex-wrap items-stretch w-full") {
                            input("block px-3 py-1.5 w-1/100 min-w-0 min-h-[52] relative flex-auto rounded-tr-none rounded-br-none  bg-transparent border-0") {
                                placeholder("Search...")
                                type("text")
                            }

                            div("p-0 border-0 border-gray-900 rounded-lg flex items-center text-center") {
                                Button(ButtonKind.SOFT, "px-6 py-3") {
                                    i("fa-solid fa-magnifying-glass text-gray-500") {}
                                }
                            }
                        }
                    }

                    div("relative order-3 lg:order-1") {
                        val dropdownOpen = storeOf(false)

                        Button(ButtonKind.PRIMARY, "mt-4 lg:mt-0") {
                            clicks.map { !dropdownOpen.current } handledBy dropdownOpen.update

                            i("fa-regular fa-grid-2 mr-2") {}
                            +"Explore"
                        }

                        ul("relative block min-w-[240px] rounded-none mt-4 lg:bg-gray-900 lg:rounded-lg lg:absolute transition ease-in-out delay-150 duration-300") {
                            className(
                                dropdownOpen.data
                                    .map { if (it) "lg:visible lg:opacity-100" else "lg:invisible lg:opacity-0" }
                            )

                            li {
                                Link(setOf("explore"), "px-6 py-3 block hover:lg:bg-dark-700") {
                                    i("fa-regular fa-hexagon-vertical-nft-slanted mr-4") { }
                                    +"All NFTs"
                                }
                            }
                        }
                    }

                    div("relative order-2 lg:order-3") {
                        ConnectionStore.isConnected.data.map { if (it) "block" else "hidden" }.let(::className)

                        val dropdownOpen = storeOf(false)

                        button {
                            type("button")

                            clicks.map { !dropdownOpen.current } handledBy dropdownOpen.update

                            img("rounded-full inline align-middle w-10 h-10") {
                                src("./assets/img/user-1.svg")
                            }
                        }

                        ul("relative block lg:right-full min-w-[240px] rounded-none mt-4 lg:bg-gray-900 lg:rounded-lg lg:absolute transition ease-in-out delay-150 duration-300") {
                            dropdownOpen.data
                                .map { if (it) "lg:visible lg:opacity-100" else "lg:invisible lg:opacity-0" }
                                .let(::className)

                            li {
                                Link(
                                    ConnectionStore.data
                                        .map {
                                            it?.walletAddress?.let { addr -> setOf("profile", addr) }
                                                ?: setOf("profile")
                                        },
                                    "px-6 py-3 block hover:lg:bg-dark-700"
                                ) {
                                    i("fa-regular fa-user mr-4") { }
                                    +"Profile"
                                }
                            }

                            li {
                                a("px-6 py-3 block hover:lg:bg-dark-700") {
                                    clicks handledBy ConnectionStore.disconnect

                                    i("fa-regular fa-link-simple-slash mr-4") { }
                                    +"Disconnect"
                                }
                            }
                        }
                    }

                    Button(ButtonKind.SOFT, "order-2 sticky bottom-4 left-0 w-full lg:w-auto") {
                        ConnectionStore.isConnected.data.map { if (it) "hidden" else "block" }.let(::className)
                        clicks handledBy PopOverStore.connect

                        i("fa-regular fa-arrow-right-to-arc mr-4") {}
                        +"Connect"
                    }
                }
            }
        }
    }
}
