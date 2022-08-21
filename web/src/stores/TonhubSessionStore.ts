import {defineStore} from "pinia";
import {TonhubConnector, TonhubCreatedSession, TonhubWalletConfig} from "ton-x";

export const useTonhubSessionStore = defineStore('tonhub_session', {
    state: () => ({
        isSessionReady: false,
        session: null as TonhubCreatedSession | null,
        walletConfig: null as TonhubWalletConfig | null,
        connector: new TonhubConnector({network: "sandbox"}),
    }),
    actions: {
        async createSession() {
            if (this.session === null) {
                this.session = await this.connector.createNewSession({name: "Libermall", url: "https://vk.com"})
            }
        },

        async sessionReady() {
            if (this.session === null) {
                throw new Error('Session not created')
            }
            const state = await this.connector.awaitSessionReady(this.session.id, 5 * 60 * 1000)

            if (state.state === 'revoked') {
                throw new Error('Connection was cancelled');
            }

            if (state.state === 'expired') {
                throw new Error('Connection was not confirmed');
            }

            this.walletConfig = state.wallet

            if (!TonhubConnector.verifyWalletConfig(this.session.id, this.walletConfig)) {
                throw new Error('Invalid configuration')
            }

            this.isSessionReady = true
        }
    }
})
