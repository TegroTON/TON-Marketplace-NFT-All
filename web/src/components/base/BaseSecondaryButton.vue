<template>
  <button
      class="px-7 py-3.5 bg-dark-700 hover:bg-gray-900 focus:bg-gray-900 border border-dark-700 hover:border-gray-900 focus:border-gray-900 rounded-lg uppercase">
    <slot></slot>
  </button>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "BaseSecondaryButton",
})
</script>
