package money.tegro.market.web.fragment

import money.tegro.market.web.component.Button
import money.tegro.market.web.html.classes
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.props.HeaderProps
import react.FC
import react.dom.html.InputType
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.header
import react.dom.html.ReactHTML.i
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.ul
import react.useState

val Header = FC<HeaderProps>("Header") { props ->
    header {
        classes = "sticky z-30 top-0 px-0 py-6 sm:py-8 bg-dark-900/[.9] backdrop-blur-2xl"

        div {
            classes = "container relative px-3 mx-auto"

            nav {
                classes = "relative flex flex-wrap p-0 items-center justify-between"
                a {
                    classes = "flex mr-12 items-center"
                    href = "/"

                    img {
                        classes = "w-12 h-12 object-contain"
                        alt = "Libermall - NFT Marketplace"
                        src = "assets/img/logo/apple-icon-57x57.png"
                    }

                    span {
                        classes = "font-raleway font-bold text-3xl hidden 2xl:block ml-6"

                        +"Libermall"
                    }
                }

                var navbarOpen by useState(false)

                Button {
                    kind = ButtonKind.SOFT
                    classes = "lg:hidden ml-4 order-2"
                    onClick = {
                        it.preventDefault()
                        navbarOpen = !navbarOpen
                    }

                    i {
                        classes = "fa-regular text-xl fa-bars"
                    }
                }

                div {
                    classes =
                        ("fixed top-0 left-0 w-3/4 h-screen bg-dark-900 lg:bg-inherit basis-full items-center px-3 py-6 lg:flex lg:basis-auto lg:p-0 lg:w-auto lg:h-auto lg:static grow items-center "
                                + if (navbarOpen) "block" else "hidden")

                    form {
                        classes = "mx-0 lg:mx-12 mb-4 lg:mb-0 order-1 lg:order-2 block grow"

                        div {
                            classes =
                                "border border-solid grow border-border-soft rounded-lg bg-soft relative flex flex-wrap items-stretch w-full"

                            input {
                                classes =
                                    "block px-3 py-1.5 w-1/100 min-w-0 min-h-[52] relative flex-auto rounded-tr-none rounded-br-none  bg-transparent border-0"

                                placeholder = "Search..."
                                type = InputType.text
                            }

                            div {
                                classes = "p-0 border-0 border-gray-900 rounded-lg flex items-center text-center"

                                Button {
                                    kind = ButtonKind.SOFT
                                    classes = "px-6 py-3"

                                    i {
                                        classes = "fa-solid fa-magnifying-glass text-gray-500"
                                    }
                                }
                            }
                        }
                    }

                    div {
                        classes = "relative order-3 lg:order-1"

                        var dropdownOpen by useState(false)

                        Button {
                            kind = ButtonKind.PRIMARY
                            classes = "mt-4 lg:mt-0"

                            onClick = {
                                it.preventDefault()
                                dropdownOpen = !dropdownOpen
                            }

                            i {
                                classes = "fa-regular fa-grid-2 mr-2"
                            }
                            +"Explore"
                        }

                        ul {
                            classes =
                                ("relative block min-w-[240px] rounded-none mt-4 lg:bg-gray-900 lg:rounded-lg lg:absolute transition ease-in-out delay-150 duration-300 "
                                        + if (dropdownOpen) "lg:visible lg:opacity-100" else "lg:invisible lg:opacity-0")

                            li {
                                a {
                                    classes = "px-6 py-3 block hover:lg:bg-dark-700"
                                    href = "/explore"

                                    i {
                                        classes = "fa-regular fa-hexagon-vertical-nft-slanted mr-4"
                                    }
                                    +"All NFTs"
                                }
                            }
                        }
                    }

                    Button {
                        kind = ButtonKind.SOFT
                        classes =
                            "order-2 sticky bottom-4 left-0 w-full lg:w-auto"
                        onClick = { props.onConnect?.invoke() }

                        i { classes = "fa-regular fa-arrow-right-to-arc mr-4" }
                        +"Connect"
                    }
                }
            }
        }
    }
}
