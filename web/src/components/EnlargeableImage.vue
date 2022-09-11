<template>
  <div>
    <img :src="src" alt="" class="item-details__image" @click="this.toggle()">
    <div v-show="isOpen" :style="{ background: 'rgba(5, 11, 28, .5) url(' + src + ') no-repeat center'}"
         class="item-details__image--size"
         @click="this.toggle()"></div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "EnlargeableImage",
  props: {
    src: String,
  },
  data() {
    return {
      isOpen: false
    }
  },
  methods: {
    toggle() {
      this.isOpen = !this.isOpen
    }
  }
})
</script>

<style lang="scss" scoped>
.item-details__image--size {
  background-size: contain;
  width: 100%;
  height: 100%;
  position: fixed;
  z-index: 10000;
  top: 0;
  left: 0;
  cursor: zoom-out;
}
</style>
