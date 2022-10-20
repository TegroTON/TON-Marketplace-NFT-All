package money.tegro.market.web.modal

import dev.fritz2.core.*
import io.ktor.client.call.*
import io.ktor.client.plugins.resources.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import money.tegro.market.model.OrdinaryItemModel
import money.tegro.market.model.TransactionRequestModel
import money.tegro.market.resource.ItemResource
import money.tegro.market.web.client
import money.tegro.market.web.component.Button
import money.tegro.market.web.formatTON
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.model.PopOver
import money.tegro.market.web.store.ConnectionStore
import money.tegro.market.web.store.PopOverStore

fun RenderContext.TransferModal(item: OrdinaryItemModel) =
    div("top-0 left-0 z-40 w-full h-full bg-dark-900/[.6]") {
        className(PopOverStore.data.map { if (it == PopOver.TRANSFER) "fixed" else "hidden" })

        div("mx-auto flex items-center relative w-auto max-w-lg min-h-screen") {
            div("bg-dark-700 rounded-3xl p-10 relative flex flex-col w-full h-full min-h-full gap-4") {
                div {
                    h5("text-2xl font-raleway font-bold mb-2") {
                        +"Transfer Item"
                    }

                    p("text-gray-500 text-lg") {
                        +"Assign ownership of an item to another account."
                    }

                    button("absolute top-6 right-8 opacity-50") {
                        type("button")
                        clicks handledBy PopOverStore.close

                        i("fa-solid fa-xmark text-2xl") {}
                    }
                }

                form("flex flex-col gap-4") {
                    val newOwnerStore = storeOf<String?>(null)

                    input("p-3 w-full rounded-xl bg-dark-900") {
                        type("text")
                        placeholder("Enter Address")
                        changes.values() handledBy newOwnerStore.update
                    }

                    ul("flex flex-col gap-2") {
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
                            +(item.transferFee + item.networkFee).formatTON().plus(" TON")
                        }
                    }

                    Button(ButtonKind.PRIMARY) {
                        clicks // On click
                            .combine(ConnectionStore.data) { _, b -> b } // Get connection state
                            .combine(newOwnerStore.data) { connection, newOwner -> connection to newOwner }
                            .mapNotNull { (a, b) -> a?.let { connection -> b?.let { newOwner -> connection to newOwner } } }
                            .map { (connection, newOwner) ->
                                client.get(
                                    ItemResource.ByAddress.Transfer(
                                        parent = ItemResource.ByAddress(address = item.address),
                                        newOwner = newOwner,
                                        response = connection.walletAddress,
                                    )
                                ).body<TransactionRequestModel>()
                            } handledBy ConnectionStore.requestTransaction

                        +"Transfer Ownership"
                    }
                }
            }
        }
    }
