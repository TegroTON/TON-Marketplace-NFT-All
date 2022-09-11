<template>
  <the-header></the-header>
  <router-view></router-view>
  <the-footer></the-footer>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import TheHeader from "./components/TheHeader.vue";
import TheFooter from "./components/TheFooter.vue";

export default defineComponent({
  name: 'App',
  components: {TheHeader, TheFooter},
})
</script>

