<template>
  <app-header></app-header>
  <router-view></router-view>
  <app-footer></app-footer>
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

<style>
@tailwind base;
@tailwind components;
@tailwind utilities;

::-webkit-scrollbar {
  @apply w-1 h-1
}

::-webkit-scrollbar-track {
  @apply bg-gray-900
}

::-webkit-scrollbar-thumb {
  @apply rounded-md bg-gray-900
}

::-webkit-scrollbar-thumb:hover {
  @apply rounded-md bg-gray-700
}
</style>

