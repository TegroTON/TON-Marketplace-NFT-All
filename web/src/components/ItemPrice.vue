<template>
  <div class="card-item-details border bg-soft p-4 rounded mb-4">
    <div class="d-flex align-items-center mb-2">
      <span class="d-block text-uppercase fs-18 fw-medium">Price:</span>
      <span class="price-item__ton d-block ms-auto">43.9 TON</span>
    </div>
    <div class="d-flex align-items-center mb-4">
      <span class="color-grey">Plus a network fee of 1 TON</span>
      <span class="price-item__dollar d-block color-grey text-end ms-auto">$64.09</span>
    </div>
    <button class="btn btn-primary w-100" data-bs-target="#BuyNowModal" data-bs-toggle="modal" type="button">Buy
      Now
    </button>
  </div>
  <div class="card-item-details d-flex flex-wrap border bg-soft p-2 rounded mb-4">
    <button class="btn btn-primary flex-fill m-2" data-bs-target="#SelectTypeModal" data-bs-toggle="modal"
            type="button">Put up for sale
    </button>
    <button class="btn btn-soft flex-fill m-2" data-bs-target="#SendFriendModal" data-bs-toggle="modal" type="button">
      Send a friend
    </button>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "ItemPrice",
})
</script>

<style lang="scss" scoped>

</style>
