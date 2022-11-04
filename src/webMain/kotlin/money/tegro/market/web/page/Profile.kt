package money.tegro.market.web.page

import dev.fritz2.core.RenderContext
import dev.fritz2.core.alt
import dev.fritz2.core.src
import dev.fritz2.core.storeOf
import money.tegro.market.resource.AllItemsResource
import money.tegro.market.web.component.Button
import money.tegro.market.web.fragment.FilterPanel
import money.tegro.market.web.fragment.ItemList
import money.tegro.market.web.fragment.SortPicker
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.normalizeAddress
import money.tegro.market.web.normalizeAndShorten

fun RenderContext.Profile(address: String) {
    section("min-w-full m-0 -mb-6 bg-gray-900") {
        picture {
            img("w-full h-[340px] object-cover align-middle") {
                src("./assets/img/profile-hero.jpg")
                alt(normalizeAddress(address))
            }
        }
    }

    main("mx-3 lg:mx-6") {
        section("container relative px-3 mx-auto gap-8 grid grid-cols-1 lg:grid-cols-3 xl:grid-cols-4") {
            val filterStore = storeOf<AllItemsResource.Filter?>(null)

            div {// Left panel
                div("flex flex-col gap-8 h-full relative top-0 -mt-24") {
                    div("rounded-lg bg-dark-700 bg-white/[.02] backdrop-blur-3xl") {// Card
                        div("flex flex-col gap-6 p-6") { // Card body
                            div("flex flex-col items-center") {
                                div { // Image
                                    img("w-full h-32 rounded-full object-cover align-middle") {
                                        src("./assets/img/user-1.svg")
                                        alt(normalizeAddress(address))
                                    }
                                }

                                // Main actions here
                            }

                            h1("text-3xl font-raleway") {
                                +normalizeAndShorten(address)
                            }
                        }
                    }

                    FilterPanel(filterStore)
                }
            }

            div("lg:col-span-2 xl:col-span-3 flex flex-col gap-6") { // Right panel
                div("overflow-auto rounded-xl bg-dark-700 bg-white/[.02] backdrop-blur-3xl flex items-center justify-between") { // Stats card
                    div("p-6 text-center flex flex-col gap-2 flex-1") {
                        h5("uppercase text-gray-500") {
                            +"Address"
                        }

                        p {
                            +normalizeAndShorten(address)
                        }
                    }
                }

                val sortStore = storeOf(AllItemsResource.Sort.INDEX_UP)
                div("flex items-center relative") {
                    ul("overflow-auto flex items-center flex-grow") { // Collection tabs
                        li {
                            Button(ButtonKind.SECONDARY, "rounded-none rounded-t-lg border-0 border-b") {
                                +"Items"
                            }
                        }
                    }

                    SortPicker(sortStore)
                }

                ItemList(
                    relatedTo = address,
                    relation = AllItemsResource.Relation.OWNERSHIP,
                    sortStore = sortStore,
                    filterStore = filterStore,
                )
            }
        }
    }
}
