<template>
  <div class="modal fade" id="EndAuctionModal" tabindex="-1" aria-labelledby="EndAuctionModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-4">
          <h5 class="modal-title fs-24" id="SelectTypeModalLabel">Bets are no longer accepted</h5>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body">
          <div class="p-3 p-md-4 border rounded text-center mb-4">
            <div class="fs-18 mb-4">Scan the QR code and pay <span class="fw-bold">0.05 TON</span> <br> to Tonkeeper
            </div>
            <img src="assets/img/qr-code.jpg" alt="" class="img-fluid rounded mb-3" width="300" height="300">
            <a href="#!" class="d-block"><i class="fa-regular fa-copy me-2"></i> Copy address</a>
          </div>
          <div class="alert alert-warning text-center mb-4">
            👉 After payment, click the button
          </div>
          <button type="button" class="btn btn-primary w-100">Check payment</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "EndAuctionModal"
}
</script>

<style scoped>

</style>
