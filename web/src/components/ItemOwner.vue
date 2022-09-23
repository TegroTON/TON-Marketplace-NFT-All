<template>
  <a class="card-item-details d-block border bg-soft hover px-4 py-3 rounded text-white" href="/profile.php">
    <h4 class="fs-14 color-grey mb-4">Owner</h4>
    <div class="d-flex align-items-center">
      <img alt="" class="img-fluid rounded-circle" height="40" src="/assets/img/author/author-12.jpg"
           width="40">
      <h4 class="collection__name fs-16 mb-0 ms-3">
        NFT Tegro
      </h4>
      <i class="fa-solid fa-angle-right color-grey ms-auto"></i>
    </div>
  </a>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "ItemOwner",
})
</script>

<style lang="scss" scoped>

</style>
