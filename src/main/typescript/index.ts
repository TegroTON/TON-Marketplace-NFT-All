import './fontawesome-pro.min'
import 'bootstrap'
import 'bootstrap/dist/css/bootstrap.css'
import '../resources/static/css/app.css'
import {createApp} from "petite-vue";

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

    get isConnected() {
        return this.provider != null && this.wallet != null
    },

    get isTonWalletAvailable() {
        return window.ton?.isTonWallet
    },

    async connectTonWallet() {
        this.provider = 'tonwallet'
        this.wallet = (await window.ton!.send('ton_requestWallets') as Wallet[])[0]
        console.log("connected as " + this.wallet.address)
    },

    disconnect() {
        this.provider = null
        this.wallet = null
    }
})
    .mount()
