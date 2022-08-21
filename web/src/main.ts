import {createApp} from 'vue'
import App from './App.vue'
import '~bootstrap'
import './assets/scss/styles.scss'
import {createPinia} from "pinia";

const app = createApp(App)
app.use(createPinia())
app.mount('#app')

app.config.unwrapInjectedRef = true
