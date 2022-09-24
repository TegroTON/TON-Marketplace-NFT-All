import {createApp} from 'vue'
import App from './App.vue'
import '~bootstrap'
import './scss/global.scss'
import {createPinia} from "pinia";
import {router} from "./routes";
import urql from '@urql/vue';

const app = createApp(App)
    .use(createPinia())
    .use(router)
    .use(urql, {
        url: 'http://localhost:8080/graphql'
    })

app.mount('#app')

app.config.unwrapInjectedRef = true
