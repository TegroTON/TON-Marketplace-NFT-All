package money.tegro.market.web.modal

import com.ionspin.kotlin.bignum.integer.BigInteger
import dev.fritz2.core.RenderContext
import dev.fritz2.core.type
import kotlinx.coroutines.flow.map
import money.tegro.market.dto.ItemDTO
import money.tegro.market.dto.TransactionRequestDTO
import money.tegro.market.web.component.Button
import money.tegro.market.web.formatTON
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.model.PopOver
import money.tegro.market.web.store.ConnectionStore
import money.tegro.market.web.store.PopOverStore

fun RenderContext.BuyModal(item: ItemDTO) =
    div("top-0 left-0 z-40 w-full h-full bg-dark-900/[.6]") {
        className(PopOverStore.data.map { if (it == PopOver.BUY) "fixed" else "hidden" })

        div("mx-auto flex items-center relative w-auto max-w-lg min-h-screen") {
            div("bg-dark-700 rounded-3xl p-10 relative flex flex-col w-full h-full min-h-full gap-4") {
                div {
                    h5("text-2xl font-raleway font-bold mb-2") {
                        +"Buy Item"
                    }

                    p("text-gray-500 text-lg") {
                        +"Remove item from sale, after which it can be transferred or put up for sale again."
                    }

                    button("absolute top-6 right-8 opacity-50") {
                        type("button")
                        clicks handledBy PopOverStore.close

                        i("fa-solid fa-xmark text-2xl") {}
                    }
                }

                form("flex flex-col gap-4") {
                    ul("flex flex-col gap-2") {
                        li("flex text-gray-500") {
                            span("flex-grow") {
                                +"Item Price"
                            }
                            span {
                                +(item.fullPrice?.formatTON()?.plus(" TON") ?: "N/A")
                            }
                        }

                        li("flex text-gray-500") {
                            span("flex-grow") {
                                +"Network Fee"
                            }
                            span {
                                +(item.minimalGasFee.formatTON() + " TON")
                            }
                        }
                    }

                    div("flex bg-soft text-lg font-medium p-4 rounded-lg") {
                        span("flex-grow") {
                            +"You'll pay"
                        }
                        span {
                            +(item.fullPrice?.plus(item.minimalGasFee) ?: BigInteger.ZERO).formatTON().plus(" TON")
                        }
                    }

                    Button(ButtonKind.PRIMARY) {
                        clicks.map {
                            TransactionRequestDTO(
                                dest = requireNotNull(item.sale),
                                value = item.fullPrice?.plus(item.minimalGasFee) ?: BigInteger.ZERO,
                                stateInit = null,
                                text = "buy",
                                payload = null,
                            )
                        } handledBy ConnectionStore.requestTransaction
                        +"Buy Item"
                    }
                }
            }
        }
    }
