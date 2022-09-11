import {createRouter, createWebHashHistory} from 'vue-router';
import Index from "./pages/Index.vue";
import Connect from "./pages/Connect.vue";

export const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {path: '/', component: Index},
        {path: '/connect', component: Connect},
    ]
})
