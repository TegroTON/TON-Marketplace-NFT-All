package money.tegro.market.web.fragment

import dev.fritz2.core.*
import kotlinx.coroutines.flow.map
import money.tegro.market.resource.AllItemsResource

fun RenderContext.FilterPanel(filterStore: RootStore<AllItemsResource.Filter?>) =
    div("relative h-full") {
        div("sticky top-36 flex flex-col gap-4 p-6") {
            h2("font-raleway font-medium text-lg") {
                +"Sale Type"
            }

            form("flex flex-col gap-2") {
                div("flex gap-2") {
                    input(id = "sale-type-all") {
                        type("radio")
                        name("sale-type")
                        checked(true)
                        changes.values()
                            .map { null } handledBy filterStore.update
                    }
                    label("text-gray-500") {
                        `for`("sale-type-all")
                        +"All Types"
                    }
                }
                div("flex gap-2") {
                    input(id = "sale-type-sale") {
                        type("radio")
                        name("sale-type")
                        changes.values()
                            .map { AllItemsResource.Filter.ON_SALE } handledBy filterStore.update
                    }
                    label("text-gray-500") {
                        `for`("sale-type-sale")
                        +"On Sale"
                    }
                }
                div("flex gap-2") {
                    input(id = "sale-type-not") {
                        type("radio")
                        name("sale-type")
                        changes.values()
                            .map { AllItemsResource.Filter.NOT_FOR_SALE } handledBy filterStore.update
                    }
                    label("text-gray-500") {
                        `for`("sale-type-not")
                        +"Not For Sale"
                    }
                }
            }
        }
    }
