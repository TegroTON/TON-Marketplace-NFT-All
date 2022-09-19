<template>
  <the-header></the-header>
  <router-view></router-view>
  <the-partners-section></the-partners-section>
  <the-footer></the-footer>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import TheHeader from "./components/TheHeader.vue";
import TheFooter from "./components/TheFooter.vue";
import ThePartnersSection from "./components/ThePartnersSection.vue";

export default defineComponent({
  name: 'App',
  components: {ThePartnersSection, TheHeader, TheFooter},
})
</script>

