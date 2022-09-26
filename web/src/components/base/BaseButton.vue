<template>
  <button
    v-if="primary && !secondary"
    :class="$attrs.class"
    class="px-6 py-3.5 text-dark-900 bg-yellow hover:bg-yellow-hover focus:bg-yellow-hover border border-yellow hover:border-yellow-hover focus:border-yellow-hover rounded-lg uppercase"
  >
    <slot></slot>
  </button>
  <button
    v-if="!primary && secondary"
    :class="$attrs.class"
    class="px-7 py-3.5 bg-dark-700 hover:bg-gray-900 focus:bg-gray-900 border border-dark-700 hover:border-gray-900 focus:border-gray-900 rounded-lg uppercase"
  >
    <slot></slot>
  </button>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "BaseButton",
  props: {
    primary: {
      type: Boolean,
    },
    secondary: {
      type: Boolean,
    },
  },
});
</script>
