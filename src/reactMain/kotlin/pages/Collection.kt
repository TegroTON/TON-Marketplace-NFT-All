package pages

import classes
import client.CollectionClient
import kotlinx.coroutines.launch
import kotlinx.js.get
import mainScope
import money.tegro.market.dto.CollectionDTO
import react.FC
import react.Props
import react.dom.html.ImgLoading
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.h5
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.main
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.picture
import react.dom.html.ReactHTML.section
import react.dom.html.ReactHTML.span
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
            classes = "container relative px-3 mx-auto gap-8 grid grid-cols-1 lg:grid-cols-3"

            div {// Left panel
                div { // Card
                    classes =
                        "relative top-0 overflow-hidden rounded-xl bg-dark-700 bg-white/[.02] backdrop-blur-3xl -mt-24"

                    div { // Card body
                        classes = "flex-auto p-6"

                        div {
                            classes = "flex flex-col items-center mb-12 gap-6"

                            div { // Image
                                img {
                                    classes = "w-full h-full rounded-full object-cover align-middle"
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
                div { // Stats card
                    classes =
                        "overflow-auto rounded-xl bg-dark-700 bg-white/[.02] backdrop-blur-3xl flex items-center content-between"

                    div {
                        classes = "tracking-tight"

                        h5 {
                            classes = "uppercase text-gray-500"
                            +"Items"
                        }

                        p {
                            classes = "uppercase"
                            +(collection?.numberOfItems ?: 0).toString()
                        }
                    }
                }
            }
        }
    }
}
