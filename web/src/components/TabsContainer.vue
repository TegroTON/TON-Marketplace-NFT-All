<template>
  <div id="myTabContent" class="tab-content py-4">
    <slot></slot>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "TabsContainer",
})
</script>
