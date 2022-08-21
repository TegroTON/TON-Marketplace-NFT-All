<template>
  <main class="main-page">
    <div v-if="this.tonhubSessionStore.isSessionReady">
      <p class="d-flex justify-content-center">
        Successfully authorized as {{ this.tonhubSessionStore.walletConfig?.address }}
      </p>
    </div>
    <div v-else>
      <p class="d-flex justify-content-center">To authorize application, scan or tap the QR code below</p>
      <a :href="tonhubConnectLink" class="d-flex justify-content-center">
        <qrcode-vue :value="tonhubConnectLink" foreground="#FFFFFF" background="#000000" :size=256></qrcode-vue>
      </a>
    </div>
  </main>
</template>

<script lang="ts">
import QrcodeVue from "qrcode.vue";
import {defineComponent} from "vue";
import {useTonhubSessionStore} from "../stores/TonhubSessionStore";

export default defineComponent({
  name: "Connect",
  components: {QrcodeVue},

  async setup() {
    const tonhubSessionStore = useTonhubSessionStore()

    await tonhubSessionStore.createSession()

    return {
      tonhubSessionStore,
      tonhubConnectLink: tonhubSessionStore.session?.link?.replace("ton-test://", "https://test.tonhub.com/")
    }
  },

  mounted() {
    this.tonhubSessionStore.sessionReady().then(
        value => {
          console.log("session ready")
        }
    )
  }
})
</script>
