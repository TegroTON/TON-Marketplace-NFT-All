import './fontawesome-pro.min'
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.css'
import '../resources/static/css/app.css'
import {createApp} from "petite-vue"
import {Address} from "ton";

interface Wallet {
    address: string;
    publicKey: string;
    walletVersion: string;
}

createApp({
    connection: {
        provider: null as string | null,
        wallet: null as Wallet | null,
    },

    loadConnection() {
        let i = localStorage.getItem('connection')
        if(i != null) {
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
})
    .mount()
