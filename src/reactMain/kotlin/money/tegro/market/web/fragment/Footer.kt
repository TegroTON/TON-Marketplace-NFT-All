package money.tegro.market.web.fragment

import money.tegro.market.web.component.Button
import money.tegro.market.web.html.classes
import money.tegro.market.web.model.ButtonKind
import react.FC
import react.Props
import react.dom.html.AnchorTarget
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.footer
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.i
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.ul

val Footer = FC<Props>("Footer") {
    footer {
        classes = "pt-24 pb-12 px-4"
        div {
            classes = "container relative mx-auto"
            div {
                classes = "flex flex-wrap px-3 grid grid-cols-1 lg:grid-cols-3 gap-12"
                div {
                    classes = "mb-12 lg:mb-0 mr-auto"

                    div {
                        classes = "flex items-center mb-6"

                        img {
                            classes = "align-middle"
                            alt = "Libermall - NFT Marketplace"
                            src = "assets/img/logo/apple-icon-57x57.png"
                        }

                        span {
                            classes = "hidden md:block text-2xl"
                            +"Libermall"
                        }
                    }

                    div {
                        classes = "mb-12"

                        h4 {
                            classes = "font-raleway mb-2 font-medium text-lg"
                            +"Stay in the loop"
                        }

                        p {
                            classes = "text-gray-500"
                            +"""
                                Follow our social media pages to stay in the loop with our newest feature releases, NFT drops,
                                and tips and tricks for navigating Libermall.
                            """.trimIndent()
                        }
                    }
                    div {
                        classes = "flex gap-3"

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
                                    href = link
                                    target = AnchorTarget._blank
                                    Button {
                                        kind = ButtonKind.SOFT
                                        classes = "p-0 w-12 h-12"

                                        i { classes = "fa-brands $icon" }
                                    }
                                }
                            }
                    }
                }
                div {
                    classes = "py-4"
                    h4 {
                        classes = "flex items-center uppercase text-lg font-medium tracking-wider mb-0"
                        +"Marketplace"
                    }

                    ul {
                        classes = "mt-6 text-gray-500"
                        li {
                            classes = "mb-2"
                            a {
                                classes = "hover:text-yellow"
                                href = "/explore"

                                +"All NFTs"
                            }
                        }
                    }
                }
            }

            div {
                classes = "text-gray-500 pt-12 mt-12"
                div {
                    classes = "flex-flex-wrap"

                    div {
                        span {
                            classes = "mr-auto"
                            +"©2022 Libermall, Inc"
                        }
                    }
                }
            }
        }
    }
}
