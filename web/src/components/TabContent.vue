<template>
  <div :aria-labelledby="by" :class="active ? 'active' : ''" class="tab-pane fade show" role="tabpanel">
    <slot></slot>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "TabContent",
  props: {
    by: {
      type: String,
    },
    active: {
      type: Boolean,
    },
  }
})
</script>

<style lang="scss" scoped>

</style>
