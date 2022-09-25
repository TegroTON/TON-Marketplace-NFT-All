<template>
  <base-secondary-button class="order-4 text-sm" @click="disconnect()">
    <i class="fa-regular fa-arrow-right-to-arc mr-2"></i> Connect
  </base-secondary-button>

  <teleport to="#modals">
    <div id="ConnectModal" aria-hidden="true" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
        <div v-if="isConnected" class="modal-content border-0">
          <div class="modal-header d-block border-0 mb-3">
            <h5 id="ConnectModalLabel" class="modal-title mb-2 fs-24">Success!</h5>
            <p class="fs-18 color-grey pe-5">
              Successfully connected
            </p>
            <button aria-label="Close" class="border-0 p-0 modal-close" data-bs-dismiss="modal" type="button"><i
                class="fa-solid fa-xmark fa-lg"></i></button>
          </div>
          <div class="modal-body">
            <p> You may now close this dialogue. </p>
          </div>
        </div>
        <div v-if="isPickingProvider" class="modal-content border-0">
          <div class="modal-header d-block border-0 mb-3">
            <h5 id="ConnectModalLabel" class="modal-title mb-2 fs-24">Connect wallet</h5>
            <p class="fs-18 color-grey pe-5">
              Choose how you want to connect
            </p>
            <button aria-label="Close" class="border-0 p-0 modal-close" data-bs-dismiss="modal" type="button"><i
                class="fa-solid fa-xmark fa-lg"></i></button>
          </div>
          <div class="modal-body">
            <a class="d-flex align-items-center border rounded bg-grey hover-grey p-3"
               type="button" @click="connectTonhub()">
              <div class="d-flex align-items-center">
                <img alt="Tonhub" class="wc-img" height="40" src="../assets/tonhub.png" width="40">
                <span class="fs-18 ms-4">Tonhub</span>
              </div>
              <div class="ms-auto">
                <i class="fa-solid fa-angle-right"></i>
              </div>
            </a>
          </div>
        </div>
        <div v-if="isConnectingTonhub" class="modal-content border-0">
          <div class="modal-header d-block border-0 mb-3">
            <h5 id="ConnectTonhubModalLabel" class="modal-title mb-2 fs-24">Connect Tonhub</h5>
            <p class="fs-18 color-grey pe-5">
              Scan the following QR code in Tonhub app
            </p>
            <button aria-label="Close" class="border-0 p-0 modal-close" data-bs-dismiss="modal" type="button"><i
                class="fa-solid fa-xmark fa-lg"></i></button>
          </div>
          <div class="modal-body d-block text-center">
            <qrcode-vue v-if="tonhubConnectionLink != null" :margin="2" :size="300" :value="tonhubConnectionLink"
                        class="mb-2"></qrcode-vue>
            <a :href="tonhubConnectionLink">
              <img src="../assets/tonhub_mini_white.svg">
            </a>
          </div>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {useConnectionStore} from "../stores/ConnectionStore";
import {useTonhubConnectionStore} from "../stores/TonhubConnectionStore";
import QrcodeVue from "qrcode.vue";
import BaseSecondaryButton from "./base/BaseSecondaryButton.vue";

export default defineComponent({
  name: "Connect",
  components: {BaseSecondaryButton, QrcodeVue},
  setup() {
    const connectionStore = useConnectionStore()
    const tonhubConnectionStore = useTonhubConnectionStore()

    return {
      connectionStore: connectionStore,
      tonhubConnectionStore: tonhubConnectionStore,
    }
  },
  computed: {
    isConnected() {
      return this.connectionStore.isConnected
    },
    isPickingProvider() {
      return !this.isConnected && this.connectionStore.provider == null
    },
    isConnectingTonhub() {
      return !this.isConnected && this.connectionStore.provider == 'tonhub'
    },
    tonhubConnectionLink() {
      return this.tonhubConnectionStore.connectionLink
    }
  },
  methods: {
    disconnect() {
      this.connectionStore.disconnect()
    },
    connectTonhub() {
      this.connectionStore.provider = 'tonhub'
      this.tonhubConnectionStore.connect()
          .then(() => {
            console.log("tonhub connected")
          })
    },
  }
})
</script>
