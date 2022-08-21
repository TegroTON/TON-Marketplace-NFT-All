import {defineStore} from "pinia";
import {TonhubConnector, TonhubCreatedSession, TonhubWalletConfig} from "ton-x";
import {useLocalStorage} from "@vueuse/core";

export const useTonhubSessionStore = defineStore('tonhub_session', {
    state: () => ({
        isSessionReady: false,
        session: useLocalStorage<TonhubCreatedSession | null>('tonhub-session', null, {
            serializer: {
                read: (v) => v ? JSON.parse(v) : null,
                write: (v) => JSON.stringify(v),
            },
        }),
        walletConfig: useLocalStorage<TonhubWalletConfig | null>('tonhub-wallet', null, {
            serializer: {
                read: (v) => v ? JSON.parse(v) : null,
                write: (v) => JSON.stringify(v),
            },
        }),
        connector: new TonhubConnector({network: "sandbox"}),
    }),
    actions: {
        async createSession() {
            if (this.session === null) {
                this.session = await this.connector.createNewSession({name: "Libermall", url: "https://vk.com"})
            }
            if (this.walletConfig === null) {

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
