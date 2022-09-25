<template>
  <ul id="myTab" class="nav collections__nav list-unstyled" role="tablist">
    <slot></slot>
  </ul>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "TabsBar",
})
</script>
