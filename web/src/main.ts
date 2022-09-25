import {createApp} from 'vue'
import App from './App.vue'
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

// ???? TODO
app.config.unwrapInjectedRef = true
