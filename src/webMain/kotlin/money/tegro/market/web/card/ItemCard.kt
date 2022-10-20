package money.tegro.market.web.card

import dev.fritz2.core.RenderContext
import dev.fritz2.core.src
import money.tegro.market.model.ItemModel
import money.tegro.market.model.OrdinaryItemModel
import money.tegro.market.model.SaleItemModel
import money.tegro.market.web.component.Link
import money.tegro.market.web.formatTON

fun RenderContext.ItemCard(item: ItemModel) =
    Link(setOf("item", item.address), "p-4 bg-dark-700 rounded-lg flex flex-col gap-4") {
        picture {
            img("w-full h-52 rounded-lg object-cover") {
                src(item.image.original ?: "assets/img/user-1.svg")
            }
        }

        h4("font-raleway text-lg") {
            +item.name
        }

        div("flex justify-between bg-soft rounded-xl p-4") {
            when (item) {
                is OrdinaryItemModel ->
                    span("w-full text-center") {
                        +"Not For Sale"
                    }

                is SaleItemModel -> {
                    span {
                        +"Price"
                    }
                    span {
                        +item.fullPrice.formatTON().plus(" TON")
                    }
                }
            }
        }
    }
