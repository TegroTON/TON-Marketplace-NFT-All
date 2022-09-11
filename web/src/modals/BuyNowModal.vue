<template>
  <div class="modal fade" id="BuyNowModal" tabindex="-1" aria-labelledby="BuyNowModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-4">
          <h5 class="modal-title fs-24" id="BuyNowModalLabel">Buy NFT</h5>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body mb-3">
          <div class="d-flex align-items-center bg-soft hover rounded p-2 mb-3">
            <img class="rounded image-80x80" src="assets/img/item-details-image.jpg" alt="Tergo Cat">
            <div class="__body ms-3">
              <h5 class="fs-18 mb-2">TON NFT Tegro Cat #101</h5>
              <p class="mb-0 color-grey fs-14">TON NFT Tegro Cat</p>
            </div>
          </div>
          <ul class="list-unstyled mb-3 px-1">
            <li class="d-flex align-items-center mb-1">
              <span>NFT Price</span>
              <span class="ms-auto">22.5 TON</span>
            </li>
            <li class="d-flex align-items-center fs-14 color-grey mb-1">
              <span>Creator Royalties</span>
              <span class="ms-auto">0.525 TON</span>
            </li>
            <li class="d-flex align-items-center fs-14 color-grey mb-3">
              <span>Service Fee</span>
              <span class="ms-auto">0.875 TON</span>
            </li>
            <li class="d-flex align-items-center mb-1">
              <span>Network Fee</span>
              <span class="ms-auto">1 TON</span>
            </li>
            <li class="d-flex align-items-center fs-14 color-grey">
              <span>The rest will be returned to your wallet</span>
            </li>
          </ul>
          <div class="d-flex align-items-center bg-soft border rounded fs-18 fw-medium p-3 mb-3">
            <span>You Pay</span>
            <span class="ms-auto">18.5 TON</span>
          </div>
          <div class="alert alert-warning" role="alert">
            <h4 class="alert-heading fs-16">Well done!</h4>
            <p class="mb-2 fs-14">
              Libermall is unaffiliated with any NFT projects. We are not responsible for possible losses. Invest at
              your own risk.
            </p>
            <a href="#!" class="color-yellow">Learn More</a>
          </div>
        </div>
        <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#ConnectModal">Buy
          for 23.5 TON
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "BuyNowModal"
}
</script>

<style scoped>

</style>
