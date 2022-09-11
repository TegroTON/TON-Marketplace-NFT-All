<template>
  <div class="modal fade" id="AlmostDoneAuctionModal" tabindex="-1" aria-labelledby="AlmostDoneAuctionModalLabel"
       aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-4">
          <h5 class="modal-title fs-24" id="SelectTypeModalLabel">Almost done</h5>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body">
          <ul class="list-unstyled mb-4">
            <li class="d-flex">
              <i class="fa-duotone fa-spinner-third fa-2x color-yellow"></i>
              <div class="ms-3">
                <h5 class="fs-16">Creation and verification</h5>
                <p class="mb-0 color-grey">
                  Confirm the transaction in your wallet. After confirmation, verification usually takes some time.
                </p>
              </div>
            </li>
          </ul>
          <div class="p-3 p-md-4 border rounded text-center mb-4">
            <h4 class="fs-18 mb-4">Scan the QR code in Tonkeeper</h4>
            <img src="assets/img/qr-code.jpg" alt="" class="img-fluid rounded mb-3" width="300" height="300">
            <a href="#!" class="color-yellow d-block"><i class="fa-regular fa-copy me-2"></i> Copy address</a>
          </div>
          <button type="button" class="btn btn-soft w-100" data-bs-dismiss="modal" aria-label="Close">Cancel</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "AlmostDoneAuctionModal"
}
</script>

<style scoped>

</style>
