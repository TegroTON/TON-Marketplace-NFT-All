package money.tegro.market.web.card

import dev.fritz2.core.RenderContext
import dev.fritz2.core.src
import money.tegro.market.model.ItemModel
import money.tegro.market.model.OrdinaryItemModel
import money.tegro.market.model.SaleItemModel
import money.tegro.market.web.component.Link
import money.tegro.market.web.formatTON

fun RenderContext.ItemCard(item: ItemModel) =
    Link(setOf("item", item.address), "bg-dark-700 rounded-lg relative flex flex-col") {
        picture {
            img("w-full h-52 rounded-t-lg object-cover") {
                src(item.image.original ?: "assets/img/user-1.svg")
            }
        }

        span("absolute top-2 right-2 p-2 rounded-lg bg-soft backdrop-blur-3xl") {
            +"#${item.index}"
        }

        div("p-4") {
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
    }
