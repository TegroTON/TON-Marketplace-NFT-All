<template>
  <Header></Header>
  <suspense>
    <router-view></router-view>
  </suspense>
  <Footer></Footer>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {RouterView} from "vue-router";
import Header from "./components/Header.vue";
import Footer from "./components/Footer.vue";

export default defineComponent({
  name: 'App',
  components: {Footer, Header, RouterView},
})
</script>

