import {createApp} from 'vue'
import App from './App.vue'
import '~bootstrap'
import './scss/global.scss'
import {createPinia} from "pinia";
import {router} from "./router";
import {ApolloClient, createHttpLink, InMemoryCache} from "@apollo/client/core";
import {createApolloProvider} from "@vue/apollo-option";

const apollo = createApolloProvider({
    defaultClient: new ApolloClient({
        link: createHttpLink({
            uri: 'http://localhost:8080/graphql'
        }),
        cache: new InMemoryCache()
    })
})

const app = createApp(App)
    .use(createPinia())
    .use(router)
    .use(apollo)

app.mount('#app')

app.config.unwrapInjectedRef = true
