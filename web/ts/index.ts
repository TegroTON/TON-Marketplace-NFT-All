import '../css/index.css'
import 'bootstrap'
import {createApp} from "petite-vue"
import {fromNano, toNano} from "ton"
import {SellModal} from "./SellModal";
import {connection} from "./Connection";
import {TransferModal} from "./TransferModal";

createApp({
    connection,

    SellModal,
    TransferModal,


    fromNano(v: any) {
        return fromNano(v)
    },
    toNano(v: number) {
        return toNano(v)
    },
})
    .mount()
