<template>
  <div id="mouseScroll" class="card card-blur mb-4 overflow-auto">
    <div class="d-flex align-items-center justify-content-between">
      <slot></slot>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "StatsBar",
})
</script>

<style lang="scss" scoped>

</style>
