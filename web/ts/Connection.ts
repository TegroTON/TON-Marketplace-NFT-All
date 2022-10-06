import {Wallet} from "./types/Wallet";
import {Address} from "ton";
import {TransactionRequest} from "./types/TransactionRequest";

export const connection = {
    connection: {
        provider: null as string | null,
        wallet: null as Wallet | null,
    },

    isModalOpen: false,

    get walletAddress() {
        return this.connection.wallet?.address
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

    async requestTransaction(req: TransactionRequest) {
        console.log(req)
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
}
