<template>
  <div class="card card-blur p-0 mt--100 mb-4">
    <div class="card__share d-flex position-absolute" style="top: 8px; right: 8px">
      <button class="btn btn-cube" type="button"><i class="fa-regular fa-share-nodes"></i></button>
      <div class="dropdown">
        <button id="dropdownMenuButton1" aria-expanded="false" class="btn btn-cube" data-bs-toggle="dropdown"
                type="button">
          <i class="fa-regular fa-ellipsis-stroke"></i>
        </button>
        <ul aria-labelledby="dropdownMenuButton1" class="dropdown-menu">
          <li><a class="dropdown-item border-0" href="#">Report page</a></li>
        </ul>
      </div>
    </div>
    <div class="card-body p-4 p-lg-2 p-xl-3 p-xxl-4">
      <div class="d-flex flex-column flex-md-row flex-lg-column align-items-center mb-5">
        <div class="collection__image mb-4 mb-md-0 mb-lg-4 ms-auto ms-md-0 ms-lg-auto me-auto">
          <slot name="image"></slot>
        </div>
        <div class="d-flex mx-0 mx-lg-auto">
          <slot name="actions"></slot>
        </div>
      </div>
      <h1 class="collection__name fs-24 mb-4">
        <slot name="name"></slot>
      </h1>
      <div class="collection__desc color-grey mb-4">
        <slot name="description"></slot>
      </div>
      <div class="libermall__soclinks">
        <a class="libermall__soclinks-item ms-0" href="#!"><i class="fa-brands fa-telegram"></i></a>
        <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-discord"></i></a>
        <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-instagram"></i></a>
        <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-linkedin-in"></i></a>
        <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-reddit-alien"></i></a>
      </div>
    </div>
    <div class="card-footer text-center border-top px-5 py-3">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "EntityInfo",
})
</script>
