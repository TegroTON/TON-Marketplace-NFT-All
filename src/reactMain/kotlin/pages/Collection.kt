package pages

import classes
import client.CollectionClient
import client.ItemClient
import components.Button
import components.ButtonKind
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.js.get
import mainScope
import money.tegro.market.dto.BasicItemDTO
import money.tegro.market.dto.CollectionDTO
import react.FC
import react.Props
import react.dom.html.ImgLoading
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h4
import react.dom.html.ReactHTML.h5
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.main
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.picture
import react.dom.html.ReactHTML.section
import react.dom.html.ReactHTML.span
import react.dom.html.ReactHTML.ul
import react.router.dom.Link
import react.router.useParams
import react.useEffectOnce
import react.useState

val Collection = FC<Props> {
    val params = useParams()
    val address = requireNotNull(params.get("address"))
    var collection: CollectionDTO? by useState(null)

    useEffectOnce {
        mainScope.launch {
            collection = CollectionClient.getByAddress(address)
        }
    }

    section {
        classes = "min-w-full m-0 -mb-6 bg-gray-900"

        picture {
            img {
                src = collection?.coverImage?.original ?: "./assets/img/nft-hero.png"
                loading = ImgLoading.lazy
                alt = collection?.name ?: "Collection Cover"
                classes = "w-full h-[340px] object-cover align-middle"
            }
        }
    }

    main {
        classes = "mx-3 lg:mx-6"

        section {
            classes = "container relative px-3 mx-auto gap-8 grid grid-cols-1 md:grid-cols-3"

            div {// Left panel
                div { // Card
                    classes =
                        "relative top-0 overflow-hidden rounded-xl bg-dark-700 bg-white/[.02] backdrop-blur-3xl -mt-24"

                    div { // Card body
                        classes = "flex flex-col gap-6 p-6"

                        div {
                            classes = "flex flex-col items-center"

                            div { // Image
                                img {
                                    classes = "w-full h-32 rounded-full object-cover align-middle"
                                    src = collection?.image?.original ?: "./assets/img/user-1.svg"
                                }
                            }

                            // Main actions here
                        }

                        h1 {
                            classes = "text-3xl font-raleway"
                            +(collection?.name ?: "...")
                        }

                        div { // Collection description
                            classes = "text-gray-500"
                            p {
                                +collection?.description.orEmpty()
                            }
                        }
                    }

                    div { // Card footer
                        classes = "text-center px-12 py-4"

                        +"Created by "
                        span {
                            classes = "text-yellow"
                            +(collection?.owner
                                ?.let { it.take(6) + "..." + it.takeLast(6) } ?: "...")
                        }
                    }
                }

                // Filters here
            }

            div { // Right panel
                classes = "md:col-span-2 flex flex-col gap-6"

                div { // Stats card
                    classes =
                        "overflow-auto rounded-xl bg-dark-700 bg-white/[.02] backdrop-blur-3xl flex items-center justify-between"

                    div {
                        classes = "p-6 text-center flex flex-col gap-2 flex-1"

                        h5 {
                            classes = "uppercase text-gray-500"
                            +"Items"
                        }

                        p {
                            classes = "uppercase"
                            +(collection?.numberOfItems ?: 0uL).toString()
                        }
                    }

                    div {
                        classes = "p-6 text-center flex flex-col gap-2 flex-1"

                        h5 {
                            classes = "uppercase text-gray-500"
                            +"Address"
                        }

                        p {
                            classes = ""
                            +collection?.address?.take(12).orEmpty()
                        }
                    }
                }

                ul { // Collection tabs
                    classes = "overflow-auto flex items-center"

                    li {
                        classes = ""

                        Button {
                            classes = "rounded-none rounded-t-lg border-0 border-b"
                            kind = ButtonKind.SECONDARY
                            +"Items"
                        }
                    }
                }

                div { // Collection Body
                    classes = "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4"

                    var items: List<BasicItemDTO> by useState(listOf())

                    useEffectOnce {
                        mainScope.launch {
                            items =
                                CollectionClient
                                    .listCollectionItems(address, drop = 0, take = 16)
                                    .map { ItemClient.getBasicInfoByAddress(it.address) }
                                    .toList()
                        }
                    }

                    for (item in items) {
                        Link {
                            key = item.address

                            classes = "p-4 bg-dark-700 rounded-lg flex flex-col gap-4"
                            to = "/item/${item.address}"

                            picture {
                                img {
                                    classes = "w-full h-52 rounded-lg object-cover"
                                    src = item.image.original ?: "./assets/img/user-1.svg"
                                }
                            }

                            h4 {
                                classes = "font-raleway text-lg"
                                +(item.name)
                            }

                            div {
                                classes = "flex justify-between bg-soft rounded-xl p-4"

                                p {
                                    classes = "w-full text-center"
                                    +"Not For Sale"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
