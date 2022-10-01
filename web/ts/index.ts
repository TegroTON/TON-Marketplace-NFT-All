import '../css/index.css'
import 'bootstrap'
import {createApp} from "petite-vue"
import {Address, fromNano, toNano} from "ton"
import {SellModal} from "./SellModal";
import {TransactionRequest} from "./types/TransactionRequest";
import {connection} from "./Connection";

createApp({
    connection,

    
    SellModal,

    async prepareItemTransferRequest(item: string, newOwner: string) {
        return await (await fetch("/api/v1/transfer?" + new URLSearchParams({
            item: item,
            newOwner: newOwner,
            responseDestination: this.connection.wallet.address,
        }))).json() as TransactionRequest
    },

    isValidAddress(address: string | null | undefined) {
        if (address != null) {
            try {
                Address.parse(address)
                return true
            } catch (e) {
            }
        }
        return false
    },

    fromNano(v: any) {
        return fromNano(v)
    },
    toNano(v: number) {
        return toNano(v)
    },
})
    .mount()
