<template>
  <section class="nft-hero">
    <slot></slot>
  </section>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "EntityHero",
})
</script>
