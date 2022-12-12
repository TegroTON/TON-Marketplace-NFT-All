package money.tegro.market.web.modal

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import dev.fritz2.core.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import money.tegro.market.model.OrdinaryItemModel
import money.tegro.market.model.TransactionRequestModel
import money.tegro.market.resource.ItemResource
import money.tegro.market.web.component.Button
import money.tegro.market.web.formatTON
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.model.PopOver
import money.tegro.market.web.store.GlobalConnectionStore
import money.tegro.market.web.store.PopOverStore
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance

fun RenderContext.SellModal(item: OrdinaryItemModel) =
    div("top-0 left-0 z-40 w-full h-full bg-dark-900/[.6]") {
        val popOverStore: PopOverStore by DI.global.instance()
        popOverStore.data.map { if (it == PopOver.SELL) "fixed" else "hidden" }.let(::className)

        div("mx-auto flex items-center relative w-auto max-w-lg min-h-screen") {
            div("bg-dark-700 rounded-3xl p-10 relative flex flex-col w-full h-full min-h-full gap-4") {
                div {
                    h5("text-2xl font-raleway font-bold mb-2") {
                        +"Sell Item"
                    }

                    p("text-gray-500 text-lg") {
                        +"Put this item up for sale."
                    }

                    button("absolute top-6 right-8 opacity-50") {
                        type("button")
                        clicks handledBy popOverStore.close

                        i("fa-solid fa-xmark text-2xl") {}
                    }
                }

                form("flex flex-col gap-4") {
                    val priceStore = storeOf(BigInteger.ZERO)
                    val royaltyAmount = storeOf(BigInteger.ZERO)
                    val marketplaceFeeAmount = storeOf(BigInteger.ZERO)
                    val fullPrice = storeOf(BigInteger.ZERO)

                    priceStore.data.map {
                        BigDecimal.fromBigInteger(it)
                            .multiply(item.royaltyValue)
                            .toBigInteger()
                    } handledBy royaltyAmount.update

                    priceStore.data.map {
                        BigDecimal.fromBigInteger(it)
                            .multiply(item.marketplaceFeeValue)
                            .toBigInteger()
                    } handledBy marketplaceFeeAmount.update

                    priceStore.data.combine(royaltyAmount.data) { a, b -> a + b }
                        .combine(marketplaceFeeAmount.data) { a, b -> a + b } handledBy fullPrice.update

                    input("p-3 w-full rounded-xl bg-dark-900") {
                        type("number")
                        placeholder("Enter Price")
                        changes.values().map { price ->
                            BigDecimal.parseString(price).multiply(BigDecimal.fromInt(1_000_000_000)).toBigInteger()
                        } handledBy priceStore.update
                    }

                    ul("flex flex-col gap-2") {
                        li("flex text-gray-500") {
                            span("flex-grow") {
                                +"Royalty"
                            }
                            royaltyAmount.data.render {
                                span {
                                    +it.formatTON().plus(" TON")
                                }
                            }
                        }

                        li("flex text-gray-500") {
                            span("flex-grow") {
                                +"Service Fee"
                            }
                            marketplaceFeeAmount.data.render {
                                span {
                                    +it.formatTON().plus(" TON")
                                }
                            }
                        }

                        li("flex text-white") {
                            span("flex-grow") {
                                +"Buyer will pay"
                            }
                            fullPrice.data.render {
                                span {
                                    +it.formatTON().plus(" TON")
                                }
                            }
                        }
                    }

                    div("flex bg-soft text-lg font-medium p-4 rounded-lg") {
                        span("flex-grow") {
                            +"You'll get"
                        }
                        priceStore.data.render {
                            span {
                                +it.formatTON().plus(" TON")
                            }
                        }
                    }

                    ul("flex flex-col gap-2") {
                        li("flex text-gray-500") {
                            span("flex-grow") {
                                +"Sale Initialization Fee"
                            }
                            span {
                                +(item.saleInitializationFee.formatTON() + " TON")
                            }
                        }

                        li("flex text-gray-500") {
                            span("flex-grow") {
                                +"Transfer Fee"
                            }
                            span {
                                +(item.transferFee.formatTON() + " TON")
                            }
                        }

                        li("flex text-gray-500") {
                            span("flex-grow") {
                                +"Network Fee"
                            }
                            span {
                                +(item.networkFee.formatTON() + " TON")
                            }
                        }
                    }

                    div("flex bg-soft text-lg font-medium p-4 rounded-lg") {
                        span("flex-grow") {
                            +"You'll pay"
                        }
                        span {
                            +(item.saleInitializationFee + item.transferFee + item.networkFee).formatTON().plus(" TON")
                        }
                    }

                    Button(ButtonKind.PRIMARY) {
                        val connectionStore: GlobalConnectionStore by DI.global.instance()
                        val httpClient: HttpClient by DI.global.instance()
                        clicks // On click
                            .combine(connectionStore.isConnected) { _, b -> b } // Get connection state
                            .filter { it } // Make sure we're connected
                            .combine(connectionStore.address) { _, a -> a } // Get address
                            .filterNotNull() // Make sure we have an address
                            .combine(priceStore.data) { address, price -> address to price }
                            .map { (seller, price) ->
                                httpClient.get(
                                    ItemResource.ByAddress.Sell(
                                        parent = ItemResource.ByAddress(address = item.address),
                                        seller = seller,
                                        price = price.toString(),
                                    )
                                ).body<TransactionRequestModel>()
                            } handledBy connectionStore.requestTransaction
                        +"Put Item For Sale"
                    }
                }
            }
        }
    }
