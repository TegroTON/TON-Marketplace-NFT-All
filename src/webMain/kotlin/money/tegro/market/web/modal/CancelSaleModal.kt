package money.tegro.market.web.modal

import dev.fritz2.core.RenderContext
import dev.fritz2.core.type
import kotlinx.coroutines.flow.map
import money.tegro.market.model.SaleItemModel
import money.tegro.market.model.TransactionRequestModel
import money.tegro.market.web.component.Button
import money.tegro.market.web.formatTON
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.model.PopOver
import money.tegro.market.web.store.ConnectionStore
import money.tegro.market.web.store.PopOverStore
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance

fun RenderContext.CancelSaleModal(item: SaleItemModel) =
    div("top-0 left-0 z-40 w-full h-full bg-dark-900/[.6]") {
        val popOverStore: PopOverStore by DI.global.instance()
        popOverStore.data.map { if (it == PopOver.CANCEL_SALE) "fixed" else "hidden" }.let(::className)

        div("mx-auto flex items-center relative w-auto max-w-lg min-h-screen") {
            div("bg-dark-700 rounded-3xl p-10 relative flex flex-col w-full h-full min-h-full gap-4") {
                div {
                    h5("text-2xl font-raleway font-bold mb-2") {
                        +"Cancel Item Sale"
                    }

                    p("text-gray-500 text-lg") {
                        +"Remove item from sale, after which it can be transferred or put up for sale again."
                    }

                    button("absolute top-6 right-8 opacity-50") {
                        type("button")
                        clicks handledBy popOverStore.close

                        i("fa-solid fa-xmark text-2xl") {}
                    }
                }

                form("flex flex-col gap-4") {
                    ul("flex flex-col gap-2") {
                        li("flex text-gray-500") {
                            span("flex-grow") {
                                +"Network Fee"
                            }
                            span {
                                +item.networkFee.formatTON().plus(" TON")
                            }
                        }
                    }

                    div("flex bg-soft text-lg font-medium p-4 rounded-lg") {
                        span("flex-grow") {
                            +"You'll pay"
                        }
                        span {
                            +item.networkFee.formatTON().plus(" TON")
                        }
                    }

                    Button(ButtonKind.PRIMARY) {
                        val connectionStore: ConnectionStore by DI.global.instance()
                        clicks.map {
                            TransactionRequestModel(
                                dest = requireNotNull(item.sale),
                                value = item.networkFee,
                                stateInit = null,
                                text = "cancel",
                                payload = null,
                            )
                        } handledBy connectionStore.requestTransaction
                        +"Cancel Item Sale"
                    }
                }
            }
        }
    }
