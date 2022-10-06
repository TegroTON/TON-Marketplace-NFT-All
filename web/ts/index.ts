import '../css/index.css'
import {createApp} from "petite-vue"
import {fromNano, toNano} from "ton"
import {SellModal} from "./modals/SellModal";
import {connection} from "./Connection";
import {TransferModal} from "./modals/TransferModal";

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
