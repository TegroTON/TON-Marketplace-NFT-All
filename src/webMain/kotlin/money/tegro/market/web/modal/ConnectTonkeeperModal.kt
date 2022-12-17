package money.tegro.market.web.modal

import QrCodeToString
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import dev.fritz2.core.type
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.js.jso
import money.tegro.market.web.component.Button
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.model.PopOver
import money.tegro.market.web.store.PopOverStore
import money.tegro.market.web.store.TonkeeperConnectionStore
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance

fun RenderContext.ConnectTonkeeperModal() =
    div("top-0 left-0 z-40 w-full h-full bg-dark-900/[.6]") {
        val popOverStore: PopOverStore by DI.global.instance()
        popOverStore.data.map { if (it == PopOver.CONNECT_TONKEEPER) "fixed" else "hidden" }.let(::className)

        div("mx-auto flex items-center relative w-auto max-w-lg min-h-screen") {
            div("bg-dark-700 rounded-3xl p-10 relative flex flex-col w-full h-full min-h-full gap-4") {
                div {
                    h5("text-2xl font-raleway font-bold mb-2") {
                        +"Connect With Tonkeeper"
                    }

                    p("text-gray-500 text-lg") {
                        +"Scan the QR code below or tap the button to connect."
                    }

                    button("absolute top-6 right-8 opacity-50") {
                        type("button")
                        clicks handledBy popOverStore.close

                        i("fa-solid fa-xmark text-2xl") {}
                    }
                }

                div("flex flex-col gap-4") {
                    val tonkeeperConnectionStore: TonkeeperConnectionStore by DI.global.instance()

                    val connectLink = flow {
                        emit(tonkeeperConnectionStore.connectLink())
                    }.shareIn(MainScope(), started = SharingStarted.Lazily, replay = 1)

                    div {
                        connectLink.map { QrCodeToString(it.orEmpty(), jso { this.type = "svg" }) }
                            .render(this) {
                                domNode.innerHTML = it
                            }
                    }

                    div("flex justify-center") {
                        a {
                            href(connectLink.map { it.orEmpty() })
                            Button(ButtonKind.PRIMARY) {
                                +"Connect"
                            }
                        }
                    }
                }
            }
        }
    }
