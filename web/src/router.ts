import {createRouter, createWebHashHistory} from 'vue-router';
import Home from "./components/Home.vue";
import Connect from "./components/Connect.vue";

export const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {path: '/', component: Home},
        {path: '/connect', component: Connect},
    ]
})
