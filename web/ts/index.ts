import {createApp} from "petite-vue"
import {fromNano, toNano} from "ton/dist/utils/convert";
import {connection} from "./Connection";
import {SellModal} from "./modals/SellModal";
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
