<template>
  <div class="modal fade" id="PutAuctionModal" tabindex="-1" aria-labelledby="PutAuctionModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-4">
          <h5 class="modal-title fs-24" id="SelectTypeModalLabel">Put up for auction</h5>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body">
          <div class="d-flex align-items-center bg-soft rounded p-2 mb-4">
            <img class="rounded image-80x80" src="assets/img/item-details-image.jpg" alt="Tergo Cat">
            <div class="__body ms-3">
              <h5 class="fs-18 mb-2">TON NFT Tegro Cat #101</h5>
              <p class="mb-0 color-grey fs-14">TON NFT Tegro Cat</p>
            </div>
          </div>
          <div class="mb-4">
            <h5 class="fs-18">Minimum bet</h5>
            <p class="color-grey">
              Bets below this amount are not allowed. You will only be able to accept the most recent high bid.
            </p>
            <div class="input-group">
              <input type="text" class="form-control" placeholder="Enter your minimum bid">
              <span class="input-group-text" id="basic-addon1">TON</span>
            </div>
          </div>
          <div class="mb-4">
            <h5 class="fs-18">Expiration date</h5>
            <p class="color-grey">
              <span>Until September 14, 17:54.</span> The auction will end automatically. The one who offers the highest
              bid wins.
            </p>
            <select class="form-select border" aria-label="Default select example">
              <option selected>7 days</option>
              <option value="1">14 days</option>
              <option value="2">1 month</option>
            </select>
          </div>
          <div class="mb-4">
            <div class="d-flex align-items-center mb-2">
              <h5 class="fs-18 mb-0">Maximum bet</h5>
              <div class="form-check form-switch ms-auto">
                <input class="form-check-input p-2" type="checkbox" id="flexSwitchCheckDefault">
              </div>
            </div>
            <p class="mb-0 color-grey">
              If someone bids that amount, the NFT will be given <br> to the buyers and the auction will be closed.
            </p>
          </div>
          <div class="mb-5">
            <div class="d-flex align-items-center mb-2">
              <h5 class="fs-18 mb-0">Step</h5>
              <div class="form-check form-switch ms-auto">
                <input class="form-check-input p-2" type="checkbox" id="flexSwitchCheckDefault">
              </div>
            </div>
            <p class="mb-0 color-grey">
              Each new bet must be higher than the previous one <br> by this amount of TON
            </p>
          </div>
          <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal"
                  data-bs-target="#AlmostDoneAuctionModal">Put up for auction
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "PutAuctionModal"
}
</script>

<style scoped>

</style>
