import {createRouter, createWebHashHistory} from "vue-router";
import HomeView from "../views/HomeView.vue";
import CollectionView from "../views/CollectionView.vue";
import ItemView from "../views/ItemView.vue";

export const router = createRouter({
    history: createWebHashHistory(),
    routes: [
        {name: 'home', path: '/', component: HomeView},
        {name: 'collection', path: '/collection/:address', component: CollectionView, props: true},
        {name: 'item', path: '/item/:address', component: ItemView, props: true},
        {name: 'explore', path: '/', component: HomeView},
        {name: 'profile', path: '/', component: HomeView},
    ],
    scrollBehavior(to, from, savedPosition) {
        return {top: 0}
    },
})
