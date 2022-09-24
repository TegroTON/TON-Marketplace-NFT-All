import {defineStore} from "pinia";
import {TonhubConnector, TonhubCreatedSession, TonhubWalletConfig} from "ton-x";
import {useLocalStorage} from "@vueuse/core";
import {TransactionRequest} from "../graphql/generated";

export const useTonhubConnectionStore = defineStore('tonhubConnection', {
    state: () => ({
        connector: new TonhubConnector({network: 'sandbox'}),
        isConnected: useLocalStorage<boolean>('tonhubConnectionConnected', false),
        session: useLocalStorage<TonhubCreatedSession | null>('tonhubConnectionSession', null, {
            serializer: {
                read: (v) => v ? JSON.parse(v) : null,
                write: (v) => JSON.stringify(v),
            },
        }),
        walletConfig: useLocalStorage<TonhubWalletConfig | null>('tonhubConnectionWalletConfig', null, {
            serializer: {
                read: (v) => v ? JSON.parse(v) : null,
                write: (v) => JSON.stringify(v),
            },
        }),
    }),
    getters: {
        connectionLink(): string | null {
            return this.session?.link ?? null
        },
        walletAddress(): string | null {
            return this.walletConfig?.address ?? null
        }
    },
    actions: {
        async connect() {
            await this.checkConnection()
            if (!this.isConnected) {
                // Create session
                this.session = await this.connector.createNewSession({name: "Libermall", url: "https://vk.com"})

                // Await confirmation
                let confirmation = await this.connector.awaitSessionReady(this.session.id, 5 * 60 * 1000)

                if (confirmation.state === 'ready') {
                    this.walletConfig = confirmation.wallet
                } else if (confirmation.state === 'expired') {
                    throw Error("Session has expired")
                } else if (confirmation.state === 'revoked') {
                    throw Error("Session has been revoked")
                } else {
                    throw Error('illegal')
                }

                await this.checkConnection()
            }
        },

        disconnect() {
            this.isConnected = false
            this.session = null
            this.walletConfig = null
        },

        async checkConnection() {
            this.isConnected = this.session !== null && // Has session
                this.walletConfig !== null && // Has wallet configuration
                (await this.connector.getSessionState(this.session.id)).state === 'ready' && // Session is active
                TonhubConnector.verifyWalletConfig(this.session.id, this.walletConfig) // Its correctly signed
        },

        async requestTransaction(options: TransactionRequest) {
            await this.checkConnection()
            if (this.isConnected && this.session !== null && this.walletConfig !== null) {
                let response = await this.connector.requestTransaction({
                    seed: this.session.seed,
                    appPublicKey: this.walletConfig.appPublicKey,
                    to: options.dest,
                    value: options.value,
                    timeout: 5 * 60 * 1000,
                    stateInit: options.stateInit,
                    text: null, // TODO
                    payload: options.payload,
                })

                if (response.type === 'success') {
                    return response.response
                } else if (response.type === 'invalid_session') {
                    throw Error("Invalid session")
                } else if (response.type === 'expired') {
                    throw Error("Expired")
                } else if (response.type === 'rejected') {
                    throw Error("Rejected")
                } else {
                    throw Error('illegal')
                }
            } else {
                throw Error("Not connected")
            }
        }
    },
})
