import {createApp} from 'vue'
import App from './App.vue'
import '~bootstrap'
import './assets/scss/styles.scss'
import {FontAwesomeIcon} from "@fortawesome/vue-fontawesome";
import {library} from "@fortawesome/fontawesome-svg-core";
import {faBars, faBorderAll, faMagnifyingGlass, faRightToBracket} from "@fortawesome/free-solid-svg-icons";

library.add(faBars, faMagnifyingGlass, faBorderAll, faRightToBracket)

createApp(App)
    .component('font-awesome-icon', FontAwesomeIcon)
    .mount('#app')
