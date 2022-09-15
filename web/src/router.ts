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
        {name: 'index', path: '/', component: Index},
        {name: 'collection', path: '/collection/:address', component: Collection, props: true},
        {name: 'create', path: '/create', component: CreateNft},
        {name: 'explore', path: '/explore', component: Explore},
        {name: 'item', path: '/item/:address', component: Item},
        {name: 'profile', path: '/profile/:address', component: Profile},
    ]
})
