import '../css/index.css'
import 'bootstrap'
import {createApp} from "petite-vue"
import {Address, fromNano, toNano} from "ton"
import BN from "bn.js";
import {Wallet} from "./types/Wallet";
import {SellModal} from "./SellModal";
import {TransactionRequest} from "./types/TransactionRequest";

export let Buffer = require('buffer').Buffer

createApp({
    SellModal,

    connection: {
        provider: null as string | null,
        wallet: null as Wallet | null,
    },

    loadConnection() {
        let i = localStorage.getItem('connection')
        if (i != null) {
            this.connection = JSON.parse(i)
        }
    },

    persistConnection() {
        localStorage.setItem('connection', JSON.stringify(this.connection))
    },

    get isConnected() {
        return this.connection.provider != null && this.connection.wallet != null
    },

    get isTonWalletAvailable() {
        return window.ton?.isTonWallet
    },

    async connectTonWallet() {
        this.connection.provider = 'tonwallet'
        this.connection.wallet = (await window.ton!.send('ton_requestWallets') as Wallet[])[0]
        this.persistConnection()
    },

    disconnect() {
        this.connection.provider = null
        this.connection.wallet = null
        this.persistConnection()
    },

    isUserWallet(address: string) {
        return this.connection.wallet != null &&
            Address.parse(address).toString() == Address.parse(this.connection.wallet?.address).toString()
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

    async requestItemTransfer(item: string, newOwner: string) {
        let req = await (await fetch("/api/v1/transfer?" + new URLSearchParams({
            item: item,
            newOwner: newOwner,
            responseDestination: this.connection.wallet.address,
        }))).json() as TransactionRequest
        await this.requestTransaction(req)
    },

    async requestItemSale(item: string, price: BN) {
        let req = await (await fetch("/api/v1/sell?" + new URLSearchParams({
            item: item,
            seller: this.connection.wallet.address,
            price: price.toString(),
        }))).json() as TransactionRequest
        await this.requestTransaction(req)
    },

    async requestTransaction(req: TransactionRequest) {
        if (this.connection.provider == 'tonwallet') {
            window.ton!.send("ton_sendTransaction", [{
                to: req.dest,
                value: req.value,
                data: req.payload ?? req.text,
                dataType: (req.payload != null) ? 'boc' : 'text',
                stateInit: req.stateInit,
            }])
        }
    },

    fromNano(v: any) {
        return fromNano(v)
    },
    toNano(v: number) {
        return toNano(v)
    },
})
    .mount()
