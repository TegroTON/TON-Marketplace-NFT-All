<template>
  <div id="ShareModal" aria-hidden="true" aria-labelledby="ShareModalLabel" class="modal fade" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-5">
          <h5 id="SelectTypeModalLabel" class="modal-title fs-24">Share</h5>
          <button aria-label="Close" class="border-0 p-0 modal-close" data-bs-dismiss="modal" type="button"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body">
          <div class="libermall__soclinks justify-content-center p-2 border rounded">
            <a :href="shareFacebook" class="libermall__soclinks-item ms-0"><i class="fa-brands fa-facebook"></i></a>
            <a :href="shareLinkedin" class="libermall__soclinks-item"><i class="fa-brands fa-linkedin-in"></i></a>
            <a :href="sharePinterest" class="libermall__soclinks-item"><i class="fa-brands fa-pinterest"></i></a>
            <a :href="shareReddit" class="libermall__soclinks-item"><i class="fa-brands fa-reddit-alien"></i></a>
            <a :href="shareTwitter" class="libermall__soclinks-item"><i class="fa-brands fa-twitter"></i></a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import ShareUrl from 'share-url';

export default defineComponent({
  name: "ShareModal",
  data() {
    return {
      fullPath: window.location.origin + this.$route.path
    }
  },
  computed: {
    shareFacebook() {
      return ShareUrl.facebook({u: this.fullPath})
    },
    shareLinkedin() {
      return ShareUrl.linkedin({url: this.fullPath})
    },
    sharePinterest() {
      return ShareUrl.pinterest({url: this.fullPath})
    },
    shareReddit() {
      return ShareUrl.reddit({url: this.fullPath})
    },
    shareTwitter() {
      return ShareUrl.twitter({url: this.fullPath})
    },
  }
})
</script>
