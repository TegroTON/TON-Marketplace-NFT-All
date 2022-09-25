<template>
  <button
      class="px-6 py-3.5 text-dark-900 bg-yellow hover:bg-yellow-hover focus:bg-yellow-hover border border-yellow hover:border-yellow-hover focus:border-yellow-hover rounded-lg uppercase">
    <slot></slot>
  </button>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "BasePrimaryButton",
})
</script>
