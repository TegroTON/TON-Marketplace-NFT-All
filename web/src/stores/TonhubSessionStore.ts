import {defineStore} from "pinia";
import {TonhubConnector, TonhubCreatedSession} from "ton-x";

export const useTonhubSessionStore = defineStore('tonhub_session', {
    state: () => ({
        connector: new TonhubConnector({network: "sandbox"}),
        session: null as TonhubCreatedSession | null,
    }),
    getters: {
        link: (state) => state.session?.link as string | null,
    },
    actions: {
        async newSession() {
            this.session = await this.connector.createNewSession({name: "Libermall", url: "https://libermall.com"})
        },
    }
})
