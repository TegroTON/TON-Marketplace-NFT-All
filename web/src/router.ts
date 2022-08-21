import {createRouter, createWebHashHistory} from 'vue-router';
import Home from "./pages/Home.vue";
import Connect from "./pages/Connect.vue";

export const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {path: '/', component: Home},
        {path: '/connect', component: Connect},
    ]
})
