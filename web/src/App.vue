<template>
  <app-header></app-header>
  <router-view></router-view>
  <app-footer></app-footer>
  <div id="modals"></div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import AppHeader from "./components/AppHeader.vue";
import AppFooter from "./components/AppFooter.vue";

export default defineComponent({
  name: 'App',
  components: {AppFooter, AppHeader},
})
</script>

