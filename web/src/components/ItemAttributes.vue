<template>
  <div class="card-item-details border bg-soft p-4 rounded mb-5">
    <h4 class="fs-18 mb-4">Attributes</h4>
    <ul class="row list-unstyled">
      <li class="col-lg-6 col-xl-4 mb-3">
        <a class="d-flex align-items-center p-3 rounded border hover text-white" href="#!" target="_blank">
          <div>
            <span class="d-block fs-14 fw-medium color-grey mb-2">Background</span>
            <span class="d-block fw-medium">Orange</span>
          </div>
          <div class="ms-auto">
            <span class="d-block fs-14 fw-medium color-grey text-end mb-2">Rarity</span>
            <div class="d-flex fs-14">
              <span>22/100</span>
              <span class="mx-1">~</span>
              <span class="fw-medium text-end">17.1%</span>
            </div>
          </div>
          <i class="fa-solid fa-angle-right color-grey ms-5"></i>
        </a>
      </li>
      <li class="col-lg-6 col-xl-4 mb-3">
        <a class="d-flex align-items-center p-3 rounded border hover text-white" href="#!" target="_blank">
          <div>
            <span class="d-block fs-14 fw-medium color-grey mb-2">Expression</span>
            <span class="d-block fw-medium">Brown Eyes</span>
          </div>
          <div class="ms-auto">
            <span class="d-block fs-14 fw-medium color-grey text-end mb-2">Rarity</span>
            <div class="d-flex fs-14">
              <span>22/100</span>
              <span class="mx-1">~</span>
              <span class="fw-medium text-end">17.1%</span>
            </div>
          </div>
          <i class="fa-solid fa-angle-right color-grey ms-5"></i>
        </a>
      </li>
      <li class="col-lg-6 col-xl-4 mb-3">
        <a class="d-flex align-items-center p-3 rounded border hover text-white" href="#!" target="_blank">
          <div>
            <span class="d-block fs-14 fw-medium color-grey mb-2">Hairstyle</span>
            <span class="d-block fw-medium">Double Ponies</span>
          </div>
          <div class="ms-auto">
            <span class="d-block fs-14 fw-medium color-grey text-end mb-2">Rarity</span>
            <div class="d-flex fs-14">
              <span>22/100</span>
              <span class="mx-1">~</span>
              <span class="fw-medium text-end">17.1%</span>
            </div>
          </div>
          <i class="fa-solid fa-angle-right color-grey ms-5"></i>
        </a>
      </li>
      <li class="col-lg-6 col-xl-4 mb-3">
        <a class="d-flex align-items-center p-3 rounded border hover text-white" href="#!" target="_blank">
          <div>
            <span class="d-block fs-14 fw-medium color-grey mb-2">Outfit</span>
            <span class="d-block fw-medium">Qipao</span>
          </div>
          <div class="ms-auto">
            <span class="d-block fs-14 fw-medium color-grey text-end mb-2">Rarity</span>
            <div class="d-flex fs-14">
              <span>22/100</span>
              <span class="mx-1">~</span>
              <span class="fw-medium text-end">17.1%</span>
            </div>
          </div>
          <i class="fa-solid fa-angle-right color-grey ms-5"></i>
        </a>
      </li>
      <li class="col-lg-6 col-xl-4 mb-3">
        <a class="d-flex align-items-center p-3 rounded border hover text-white" href="#!" target="_blank">
          <div>
            <span class="d-block fs-14 fw-medium color-grey mb-2">Prop</span>
            <span class="d-block fw-medium">Lolipop</span>
          </div>
          <div class="ms-auto">
            <span class="d-block fs-14 fw-medium color-grey text-end mb-2">Rarity</span>
            <div class="d-flex fs-14">
              <span>22/100</span>
              <span class="mx-1">~</span>
              <span class="fw-medium text-end">17.1%</span>
            </div>
          </div>
          <i class="fa-solid fa-angle-right color-grey ms-5"></i>
        </a>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "ItemAttributes",
})
</script>

<style lang="scss" scoped>

</style>
