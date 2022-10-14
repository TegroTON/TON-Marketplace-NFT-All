package money.tegro.market.web.page


import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.js.get
import money.tegro.market.dto.CollectionDTO
import money.tegro.market.dto.ItemDTO
import money.tegro.market.web.client.APIv1Client
import money.tegro.market.web.html.classes
import money.tegro.market.web.mainScope
import react.FC
import react.Props
import react.dom.html.AnchorTarget
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.i
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.main
import react.dom.html.ReactHTML.nav
import react.dom.html.ReactHTML.ol
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.section
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.ul
import react.router.dom.Link
import react.router.useParams
import react.useEffectOnce
import react.useState

val Item = FC<Props>("Item") {
    val params = useParams()
    val address = requireNotNull(params.get("address"))
    var item: ItemDTO? by useState(null)
    var collection: CollectionDTO? by useState(null)

    useEffectOnce {
        mainScope.launch {
            val asyncItem = async { APIv1Client.getItem(address) }
            val asyncCollection = async { asyncItem.await().collection?.let { APIv1Client.getCollection(it) } }

            item = asyncItem.await()
            collection = asyncCollection.await()
        }
    }

    main {
        classes = "mx-3 lg:mx-6"

        section {
            classes = "container relative pt-12 mx-auto flex flex-col gap-12 justify-center"

            nav {
                ol {
                    classes = "flex flex-wrap text-gray-500 gap-2"
                    li {
                        Link {
                            to = "/explore"
                            +"Explore"
                        }
                    }
                    if (collection != null) {
                        li {
                            span {
                                +"/"
                            }
                        }
                        li {
                            Link {
                                to = "/collection/${collection?.address}"
                                +(collection?.name ?: "Collection")
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
                            +(item?.name ?: "This Item")
                        }
                    }
                }
            }

            div {
                classes = "grid gap-12 grid-cols-1 lg:grid-cols-3"

                div {
                    img {
                        classes = "w-full h-auto object-cover rounded-2xl"
                        src = item?.image?.original ?: "./assets/img/user-1.svg"
                    }
                }

                div {
                    classes = " lg:col-span-2 flex flex-col gap-8"

                    div {
                        classes = "flex gap-2"
                        if (item?.sale != null) {
                            div {
                                classes = "px-6 py-3 text-green bg-green-soft rounded-2xl uppercase"
                                +"For Sale"
                            }
                        } else {
                            div {
                                classes = "px-6 py-3 text-white bg-soft rounded-2xl uppercase"
                                +"Not For Sale"
                            }
                        }
                    }

                    div {
                        classes = "flex flex-col gap-4 px-4"

                        h1 {
                            classes = "text-3xl font-raleway font-medium"
                            +(item?.name ?: "Item")
                        }

                        p {
                            classes = "text-gray-500"
                            +item?.description.orEmpty()
                        }
                    }

                    // Actions here

                    div {
                        classes = "grid grid-cols-1 lg:grid-cols-2 gap-4"

                        Link {
                            classes = "rounded-lg bg-soft px-6 py-4 flex-grow flex flex-col gap-2"
                            to = "/profile/${item?.owner}"

                            h4 {
                                classes = "font-raleway text-sm text-gray-500"
                                +"Owner"
                            }

                            div {
                                classes = "flex items-center gap-2"

                                img {
                                    classes = "w-10 h-10 rounded-full"
                                    src = "./assets/img/user-1.svg"
                                }

                                h4 {
                                    classes = "flex-grow text-lg"
                                    +(item?.owner?.take(12)?.plus("...") ?: "Owner")
                                }

                                i {
                                    classes = "fa-solid fa-angle-right"
                                }
                            }
                        }

                        Link {
                            classes = "rounded-lg bg-soft px-6 py-4 flex-grow flex flex-col gap-2"
                            to = collection?.address?.let { "/collection/$it" } ?: "/explore"

                            h4 {
                                classes = "font-raleway text-sm text-gray-500"
                                +"Collection"
                            }

                            div {
                                classes = "flex items-center gap-2"

                                img {
                                    classes = "w-10 h-10 rounded-full"
                                    src = collection?.image?.original ?: "./assets/img/user-1.svg"
                                }

                                h4 {
                                    classes = "flex-grow text-lg"
                                    +(collection?.name ?: "Not In Collection")
                                }

                                i {
                                    classes = "fa-solid fa-angle-right"
                                }
                            }
                        }
                    }

                    div {
                        classes = "rounded-lg bg-soft px-6 py-4 flex flex-col gap-2"

                        h4 {
                            classes = "text-lg font-raleway"
                            +"Details"
                        }

                        ul {
                            classes = "flex flex-col gap-2"

                            li {
                                a {
                                    classes = "p-4 flex gap-2 items-center rounded-lg border border-gray-900"
                                    target = AnchorTarget._blank
                                    href = "https://testnet.tonscan.org/address/${item?.address}"

                                    span {
                                        classes = "text-gray-500"
                                        +"Contract Address"
                                    }

                                    span {
                                        classes = "flex-grow text-right"
                                        +(item?.address?.take(12)?.plus("...") ?: "n/a")
                                    }

                                    i { classes = "fa-solid fa-angle-right" }
                                }
                            }

                            if (item?.sale != null) {
                                li {
                                    a {
                                        classes = "p-4 flex gap-2 items-center rounded-lg border border-gray-900"
                                        target = AnchorTarget._blank
                                        href = "https://testnet.tonscan.org/address/${item?.sale}"

                                        span {
                                            classes = "text-gray-500"
                                            +"Sale Address"
                                        }

                                        span {
                                            classes = "flex-grow text-right"
                                            +(item?.sale?.take(12)?.plus("...") ?: "n/a")
                                        }

                                        i { classes = "fa-solid fa-angle-right" }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (!item?.attributes.isNullOrEmpty()) {
                div { // Attributes
                    classes = "rounded-lg bg-soft px-6 py-4 flex flex-col gap-4"

                    h4 {
                        classes = "text-lg font-raleway"
                        +"Attributes"
                    }

                    ul {
                        classes = "grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-2"

                        for ((trait, value) in item?.attributes.orEmpty()) {
                            li {
                                key = trait

                                a {
                                    classes = "p-4 flex gap-2 items-center rounded-lg border border-gray-900"
                                    target = AnchorTarget._blank
                                    href = "#"

                                    span {
                                        classes = "text-gray-500"
                                        +trait
                                    }

                                    span {
                                        classes = "flex-grow text-right"
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
