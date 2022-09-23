<template>
  <li class="collections__nav-item">
    <button :aria-controls="target" :aria-selected="active" :class="active ? 'active' : ''"
            :data-bs-target="target"
            class="collections__nav-link d-flex align-items-center text-nowrap" data-bs-toggle="tab" role="tab"
            type="button">
      <slot></slot>
    </button>
  </li>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "TabBarButton",
  props: {
    target: {
      type: String,
      required: true,
    },
    active: {
      type: Boolean,
    },
  }
})
</script>

<style lang="scss" scoped>

</style>
