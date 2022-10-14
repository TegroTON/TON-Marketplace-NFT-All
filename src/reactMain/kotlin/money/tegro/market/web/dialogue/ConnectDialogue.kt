package money.tegro.market.web.dialogue

import money.tegro.market.web.component.Button
import money.tegro.market.web.html.classes
import money.tegro.market.web.model.ButtonKind
import money.tegro.market.web.props.ConnectDialogueProps
import react.FC
import react.dom.html.ButtonType
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h5
import react.dom.html.ReactHTML.i
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span

val ConnectDialogue = FC<ConnectDialogueProps>("ConnectDialogue") { props ->
    div {
        classes =
            "top-0 left-0 z-40 w-full h-full bg-dark-900/[.6] " + if (props.open == true) "fixed" else "hidden"

        div {
            classes = "mx-auto flex items-center relative w-auto max-w-lg min-h-screen"

            div {
                classes = "bg-dark-700 rounded-3xl p-10 relative flex flex-col w-full h-full min-h-full gap-4"

                div {
                    h5 {
                        classes = "text-2xl font-raleway font-bold mb-2"
                        +"Connect Wallet"
                    }

                    p {
                        classes = "text-gray-500 text-lg"
                        +"Choose how you want to connect. More options will be added in the future."
                    }

                    button {
                        classes = "absolute top-6 right-8 opacity-50"
                        type = ButtonType.button
                        onClick = { props.onClose?.invoke() }

                        i { classes = "fa-solid fa-xmark text-2xl" }
                    }
                }

                div {
                    classes = "flex flex-col"

                    Button {
                        classes = "flex items-center gap-4"
                        kind = ButtonKind.SOFT

                        img {
                            classes = "w-10 h-10"
                            alt = "Ton Wallet"
                            src = "./assets/img/ton-wallet.png"
                        }

                        span {
                            classes = "text-lg flex-grow"
                            +"Ton Wallet"
                        }

                        i { classes = "fa-solid fa-angle-right" }
                    }
                }
            }
        }
    }
}
