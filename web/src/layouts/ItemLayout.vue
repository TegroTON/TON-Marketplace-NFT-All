<template>
  <main class="main-page">
    <section class="item-details section pt-5">
      <div class="container-fluid">
        <div class="row justify-content-center">
          <div class="col-md-10 col-lg-12 col-xxl-9">
            <slot name="breadcrumb"></slot>
            <div class="row justify-content-center mb-4">
              <div class="col-lg-5 col-xl-5 mb-4 mb-lg-0">
                <!-- Start NFT Image -->
                <div class="position-sticky" style="top: 140px;">
                  <slot name="content"></slot>
                </div>
                <!-- // End NFT Image -->
              </div>
              <div class="col-lg-7 col-xl-7">
                <slot name="details"></slot>
              </div>
            </div>
            <slot name="extra"></slot>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "ItemLayout",
})
</script>
