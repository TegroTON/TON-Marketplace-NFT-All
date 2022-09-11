import {createApp} from 'vue'
import App from './App.vue'
import './scss/global.scss'
import {createPinia} from "pinia";
import {router} from "./router";

const app = createApp(App)
    .use(createPinia())
    .use(router)

app.mount('#app')

app.config.unwrapInjectedRef = true
