package pages

import classes
import client.CollectionClient
import components.Button
import components.ButtonKind
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import mainScope
import money.tegro.market.dto.TopCollectionDTO
import react.FC
import react.Props
import react.dom.html.AnchorTarget
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.i
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.main
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.picture
import react.dom.html.ReactHTML.section
import react.dom.html.ReactHTML.source
import react.dom.html.ReactHTML.span
import react.useEffectOnce
import react.useState

val Index = FC<Props>("Index") {
    main {
        classes = "mx-3 lg:mx-6"

        section {
            classes = "px-0 py-12"

            div {
                classes = "container relative overflow-hidden rounded-3xl mx-auto"

                var slide by useState(0)

                div {
                    classes = "absolute bottom-0 left-0 right-0 p-0 mb-4 flex justify-center"

                    button {
                        classes = ("z-10 w-3 h-3 m-1 rounded-full flex-initial "
                                + if (slide == 0) "bg-yellow" else "bg-gray-500")
                        onClick = {
                            slide = 0
                        }
                    }

                    button {
                        classes = ("z-10 w-3 h-3 m-1 rounded-full flex-initial "
                                + if (slide == 1) "bg-yellow" else "bg-gray-500")
                        onClick = {
                            slide = 1
                        }
                    }
                }

                div {
                    classes = "relative overflow-hidden"

                    div {
                        classes = ("relative px-6 pt-48 pb-12 "
                                + if (slide == 0) "block" else "hidden")

                        div {
                            classes = "flex flex-wrap items-center lg:px-20 lg:py-16"

                            div {
                                classes = "w-full text-center lg:text-start"

                                h1 {
                                    classes = "text-5xl font-bold font-raleway mb-6"

                                    +"Witness the birth of Libermall"
                                }

                                div {
                                    classes = "text-lg mb-15"

                                    +"A new, modern and slick NFT marketplace is emerging on the open network"
                                }
                            }
                        }
                        picture {
                            source {
                                srcSet = "./assets/img/hero-image-2.webp"
                                type = "image/webp"
                            }
                            source {
                                srcSet = "./assets/img/hero-image-2.jpg"
                                type = "image/jpeg"
                            }
                            img {
                                alt = "Libermall - NFT Marketplace"
                                src = "./assets/img/hero-image-2.jpg"
                                classes = "absolute -z-10 top-0 right-0 w-full h-full object-cover opacity-50"
                            }
                        }
                    }

                    div {
                        classes = ("relative px-6 pt-48 pb-12 "
                                + if (slide == 1) "block" else "hidden")

                        div {
                            classes = "flex flex-wrap items-center lg:px-20 lg:py-16"

                            div {
                                classes = "w-full text-center lg:text-start"

                                h1 {
                                    classes = "text-5xl font-bold font-raleway mb-6"

                                    +"Witness the birth of Libermall"
                                }

                                div {
                                    classes = "text-lg mb-15"

                                    +"A new, modern and slick NFT marketplace is emerging on the open network"
                                }
                            }
                        }
                        picture {
                            source {
                                srcSet = "./assets/img/hero-image.webp"
                                type = "image/webp"
                            }
                            source {
                                srcSet = "./assets/img/hero-image.jpg"
                                type = "image/jpeg"
                            }
                            img {
                                alt = "Libermall - NFT Marketplace"
                                src = "./assets/img/hero-image.jpg"
                                classes = "absolute -z-10 top-0 right-0 w-full h-full object-cover opacity-50"
                            }
                        }
                    }
                }
            }
        }

        section {
            classes = "px-0 py-12 pt-28"
            div {
                classes = "container relative text-center mx-auto"

                h2 {
                    classes = "text-5xl font-raleway font-bold mb-4"
                    +"Discover, Collect and sell "
                    span {
                        classes = "text-yellow"
                        +"extraordinary NFTs"
                    }
                }
                p {
                    classes = "mb-12 text-gray-500 text-2xl font-light"
                    +"Libermall is an emerging NFT marketplace"
                }

                div {
                    classes = "flex flex-wrap items-center justify-center"

                    Button {
                        kind = ButtonKind.PRIMARY
                        classes =
                            "text-dark-900 bg-yellow px-8 py-4 m-0 border-0 rounded-lg uppercase font-medium text-sm flex flex-nowrap items-center tracking-widest hover:bg-yellow-hover hover:border-yellow-hover"
                        +"Explore"
                    }
                }
            }
        }

        section {
            classes = "px-0 py-12"

            div {
                classes =
                    "relative container px-10 py-10 mx-auto overflow-hidden rounded-lg bg-gradient-to-r from-yellow-gradient-start to-yellow-gradient-end"

                span {
                    classes =
                        "block absolute top-0 w-full h-full right-[-70%] md:right-[-80%] xl:right-[-45%]  2xl:right-[-40%] bg-left-top bg-cover "
                }

                h2 {
                    classes = "text-dark-900 text-3xl font-raleway mb-4"

                    +"Early Access "
                    span {
                        classes = "block md:inline"
                        +"on Libermall"
                    }
                }

                div {
                    classes = "mt-10 flex items-center"
                    a {
                        href = "/explore"
                        Button {
                            kind = ButtonKind.PRIMARY
                            +"Explore"
                        }
                    }

                    div {
                        classes = "hidden md:flex gap-3 ml-12"

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
                                        kind = ButtonKind.SECONDARY
                                        classes = "p-0 w-12 h-12 hover:bg-dark-900 focus:bg-dark-900 border-dark-900"

                                        i {
                                            classes = "fa-brands $icon"
                                        }
                                    }
                                }
                            }
                    }
                }
            }
        }

        section {
            classes = "px-0 py-12"
            div {
                classes = "container relative mx-auto"
                div {
                    classes = "block md:flex mb-14"

                    h2 {
                        classes = "text-4xl font-bold font-raleway mb-0"

                        +"Top "
                        span {
                            classes = "text-yellow"
                            +"collections"
                        }
                    }

                    div {
                        classes = "mt-6 md:mt-0 ml-auto"
                        Button {
                            kind = ButtonKind.SOFT
                            +"All Time"
                        }
                    }
                }

                div {
                    classes = "pt-4 flex flex-wrap gap-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3"

                    var topCollections: List<TopCollectionDTO> by useState(listOf())

                    useEffectOnce {
                        mainScope.launch {
                            topCollections = CollectionClient.listTopCollections().toList()
                        }
                    }

                    for ((index, collection) in topCollections.withIndex()) {
                        a {
                            key = collection.address

                            classes =
                                "flex flex-col lg:flex-row gap-4 rounded-xl p-4 items-center bg-dark-700 hover:bg-gray-900"
                            href = "/collection/" + collection.address
                            title = collection.name

                            span {
                                classes = "font-bold"
                                +"${index + 1}" // Index
                            }

                            picture {
                                img {
                                    alt = collection.name
                                    classes = "rounded-full w-16 h-16"
                                    src = collection.image.original
                                }
                            }

                            h4 {
                                classes = "text-lg font-raleway"
                                +collection.name
                            }
                        }
                    }
                }

                div {
                    classes = "mt-12 flex flex-wrap items-center justify-center"
                    a {
                        href = "/explore"
                        Button {
                            kind = ButtonKind.PRIMARY
                            +"Explore"
                        }
                    }
                }
            }
        }
    }
}
