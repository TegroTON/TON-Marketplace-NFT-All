<template>
  <div class="card-blur__item p-4 border-end text-center">
    <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">
      <slot name="name"></slot>
    </h5>
    <p :class="preserveCase ? '' : 'text-uppercase'" class="m-0 fw-medium" style="letter-spacing: 1px;">
      <slot name="value"></slot>
    </p>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "StatsBarEntry",
  props: {
    preserveCase: {
      type: Boolean,
    }
  }
})
</script>
