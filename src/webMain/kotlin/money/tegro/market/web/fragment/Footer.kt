package money.tegro.market.web.fragment

import dev.fritz2.core.*
import money.tegro.market.web.component.Button
import money.tegro.market.web.component.Link
import money.tegro.market.web.model.ButtonKind

fun RenderContext.Footer() {
    footer("pt-24 pb-12 px-4") {
        div("container relative mx-auto") {
            div("flex flex-wrap px-3 grid grid-cols-1 lg:grid-cols-3 gap-12") {
                div("mb-12 lg:mb-0") {
                    div("flex items-center mb-6") {
                        img("align-middle w-12 h-12") {
                            alt("Libermall - NFT Marketplace")
                            src("./assets/img/logo/large.png")
                        }

                        span("hidden md:block text-2xl") {
                            +"Libermall"
                        }
                    }

                    div("mb-12") {
                        h4("font-raleway mb-2 font-medium text-lg") {
                            +"Stay in the loop"
                        }

                        p("text-gray-500") {
                            +"""
                                Follow our social media pages to stay in the loop with our newest feature releases, NFT drops,
                                and tips and tricks for navigating Libermall.
                            """.trimIndent()
                        }
                    }
                    div("flex flex-wrap gap-3") {
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
                                    Button(ButtonKind.SOFT, "p-0 w-12 h-12") {
                                        i("fa-brands $icon") { }
                                    }
                                }
                            }
                    }
                }
                div("py-4") {
                    h4("flex items-center uppercase text-lg font-medium tracking-wider mb-0") {
                        +"Marketplace"
                    }

                    ul("mt-6 text-gray-500") {
                        li("mb-2") {
                            Link(setOf("explore"), "hover:text-yellow") {
                                +"All NFTs"
                            }
                        }
                    }
                }
            }

            div("text-gray-500 pt-12 mt-12") {
                div("flex-flex-wrap") {
                    div {
                        span("mr-auto") {
                            +"©2022 Libermall, Inc"
                        }
                    }
                }
            }
        }
    }
}
