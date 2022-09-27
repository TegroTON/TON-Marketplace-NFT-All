<template>
  <teleport to="#modals">
    <div
      v-if="open"
      class="fixed top-0 left-0 z-40 w-screen h-screen opacity-50 bg-dark-700"
    >
      <div
        class="container z-50 p-10 bg-dark-700 rounded-3xl border-0 flex w-full flex-col"
      >
        <slot></slot>
      </div>
    </div>
  </teleport>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "BaseModal",
  props: {
    open: {
      type: Boolean,
    },
  },
});
</script>
