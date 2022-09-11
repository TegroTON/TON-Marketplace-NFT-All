<template>
  <div class="modal fade" id="ConnectModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header d-block border-0 mb-3">
          <h5 class="modal-title mb-2 fs-24" id="ConnectModalLabel">Connect wallet</h5>
          <p class="fs-18 color-grey pe-5">
            Choose how you want to connect. There are several wallet providers.
          </p>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body">
          <a class="d-flex align-items-center border rounded bg-grey hover-grey mb-3 p-3" href="/profile.php">
            <div class="d-flex align-items-center">
              <img src="assets/img/ton-wallet.png" alt="Ton Wallet" width="40" height="40" class="wc-img">
              <span class="fs-18 ms-4">Ton Wallet</span>
            </div>
            <div class="ms-auto">
              <i class="fa-solid fa-angle-right"></i>
            </div>
          </a>
          <a class="d-flex align-items-center border rounded bg-grey hover-grey mb-3 p-3" href="#!">
            <div class="d-flex align-items-center">
              <img src="assets/img/tonkeeper.png" alt="Tonkeeper" width="40" height="40" class="wc-img">
              <span class="fs-18 ms-4">Tonkeeper</span>
            </div>
            <div class="ms-auto">
              <i class="fa-solid fa-angle-right"></i>
            </div>
          </a>
          <a class="d-flex align-items-center border rounded bg-grey hover-grey p-3" href="#!">
            <div class="d-flex align-items-center">
              <img src="assets/img/tonhub.png" alt="Tonhub" width="40" height="40" class="wc-img">
              <span class="fs-18 ms-4">Tonhub</span>
            </div>
            <div class="ms-auto">
              <i class="fa-solid fa-angle-right"></i>
            </div>
          </a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ConnectModal"
}
</script>

<style scoped>

</style>
