import {defineStore} from "pinia";
import {useLocalStorage} from "@vueuse/core";
import {useTonhubConnectionStore} from "./TonhubConnectionStore";
import {TransactionRequest} from "../graphql/generated";

export const useConnectionStore = defineStore('connection', {
    state: () => ({
        provider: useLocalStorage<string>('connectionProvider', null),
    }),
    getters: {
        availableProviders() {
            return ['tonhub']
        },

        isConnected() {
            if (this.provider === 'tonhub') {
                return useTonhubConnectionStore().isConnected
            } else {
                return false
            }
        },

        walletAddress() {
            if (this.provider === 'tonhub') {
                return useTonhubConnectionStore().walletAddress
            } else {
                return null
            }
        }
    },
    actions: {
        disconnect() {
            if (this.provider === 'tonhub') {
                useTonhubConnectionStore().disconnect()
            } else {
                // nil
            }
            this.provider = null
        },

        async requestTransaction(options: TransactionRequest) {
            if (this.provider === 'tonhub') {
                await useTonhubConnectionStore().requestTransaction(options)
            } else {
                // nil
            }
        }
    }
})