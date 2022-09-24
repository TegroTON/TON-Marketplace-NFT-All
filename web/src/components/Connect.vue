<template>
  <button
      class="btn btn-soft d-flex flex-nowrap align-items-center btn-mobile-fixed order-3 order-lg-4"
      data-bs-target="#ConnectModal"
      data-bs-toggle="modal" type="button">
    <i class="fa-regular fa-arrow-right-to-arc me-2"></i> Connect
  </button>

  <teleport to="#modals">
    <div id="ConnectModal" aria-hidden="true" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
        <div class="modal-content border-0">
          <div class="modal-header d-block border-0 mb-3">
            <h5 id="ConnectModalLabel" class="modal-title mb-2 fs-24">Connect wallet</h5>
            <p class="fs-18 color-grey pe-5">
              Choose how you want to connect
            </p>
            <button aria-label="Close" class="border-0 p-0 modal-close" data-bs-dismiss="modal" type="button"><i
                class="fa-solid fa-xmark fa-lg"></i></button>
          </div>
          <div class="modal-body">
            <connect-tonhub v-if="isTonhubAvailable"></connect-tonhub>
          </div>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {mapState} from "pinia";
import {useConnectionStore} from "../stores/ConnectionStore";
import ConnectTonhub from "./ConnectTonhub.vue";

export default defineComponent({
  name: "Connect",
  components: {ConnectTonhub},
  computed: {
    ...mapState(useConnectionStore, {
      isConnected: 'isConnected',
      isTonhubAvailable: (store) => store.availableProviders.includes('tonhub'),
    }),
  },
})
</script>

<style lang="scss" scoped>

</style>
