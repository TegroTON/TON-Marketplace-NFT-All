<template>
  <a class="d-flex align-items-center border rounded bg-grey hover-grey p-3"
     data-bs-target="#ConnectTonhubModal"
     data-bs-toggle="modal" type="button"
     @click="connectTonhub">
    <div class="d-flex align-items-center">
      <img alt="Tonhub" class="wc-img" height="40" src="../assets/tonhub.png" width="40">
      <span class="fs-18 ms-4">Tonhub</span>
    </div>
    <div class="ms-auto">
      <i class="fa-solid fa-angle-right"></i>
    </div>
  </a>

  <teleport to="#modals">
    <div v-if="!isConnected" id="ConnectTonhubModal" aria-hidden="true" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
        <div class="modal-content border-0">
          <div class="modal-header d-block border-0 mb-3">
            <h5 id="ConnectTonhubModalLabel" class="modal-title mb-2 fs-24">Connect Tonhub</h5>
            <p class="fs-18 color-grey pe-5">
              Scan the following QR code in Tonhub app
            </p>
            <button aria-label="Close" class="border-0 p-0 modal-close" data-bs-dismiss="modal" type="button"><i
                class="fa-solid fa-xmark fa-lg"></i></button>
          </div>
          <div class="modal-body d-block text-center">
            <qrcode-vue v-if="typeof connectionLink === 'string'" :margin="2" :size="300" :value="connectionLink"
                        class="mb-2"></qrcode-vue>
            <a :href="connectionLink">
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
import {mapActions, mapState, mapWritableState} from "pinia";
import {useConnectionStore} from "../stores/ConnectionStore";
import {useTonhubConnectionStore} from "../stores/TonhubConnectionStore";
import QrcodeVue from "qrcode.vue";

export default defineComponent({
  name: "ConnectTonhub",
  components: {QrcodeVue},
  computed: {
    ...mapState(useTonhubConnectionStore, ['isConnected', 'connectionLink']),
    ...mapWritableState(useConnectionStore, ['provider']),
  },
  methods: {
    ...mapActions(useTonhubConnectionStore, ['connect']),
    connectTonhub() {
      this.provider = 'tonhub'
      this.connect()
          .then(() => {
            console.log("tonhub connected")
            // TODO: Fucking fuck, due to parent component being destroyed as soon as wallet is connected, this leaves
            // the stupid bootstrap's modal backdrop on. This is a simple but yucky solution - to simply refresh the page
            this.$router.go()
          })
    },
  },
})
</script>

<style lang="scss" scoped>

</style>
