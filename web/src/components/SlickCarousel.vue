<template>
  <div>
    <slot></slot>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  props: {
    options: {
      type: Object,
      default: function () {
        return {};
      },
    },
  },
})
</script>
