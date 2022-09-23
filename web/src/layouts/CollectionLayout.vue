<template>
  <entity-hero>
    <slot name="hero"></slot>
  </entity-hero>
  <main class="main-page">
    <section class="nfr-collection section pt-0">
      <div class="container-fluid">
        <div class="row">
          <div class="col-lg-4 col-xl-4 col-xxl-3 mb-4 mb-lg-0">
            <slot name="left-panel"></slot>
          </div>
          <div class="col-lg-8 col-xl-8 col-xxl-9">
            <slot name="right-panel"></slot>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import EntityHero from "../components/EntityHero.vue";

export default defineComponent({
  name: "CollectionLayout",
  components: {EntityHero},
})
</script>

<style lang="scss" scoped>

</style>
