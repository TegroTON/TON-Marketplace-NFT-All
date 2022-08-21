import {createApp} from 'vue'
import App from './App.vue'
import '~bootstrap'
import './assets/scss/styles.scss'
import {createPinia} from "pinia";
import {router} from "./router";

const app = createApp(App)
app.use(createPinia())
app.use(router)
app.mount('#app')

app.config.unwrapInjectedRef = true
