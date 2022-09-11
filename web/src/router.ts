import {createRouter, createWebHashHistory} from 'vue-router';
import Index from "./pages/Index.vue";
import Collection from "./pages/Collection.vue";
import CreateNft from "./pages/CreateNft.vue";
import Explore from "./pages/Explore.vue";
import Item from "./pages/Item.vue";
import Profile from "./pages/Profile.vue";

export const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {path: '/', component: Index},
        {path: '/collection/:address', component: Collection},
        {path: '/create', component: CreateNft},
        {path: '/explore', component: Explore},
        {path: '/item/:address', component: Item},
        {path: '/profile/:address', component: Profile},
    ]
})
