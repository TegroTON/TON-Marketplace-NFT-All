<template>
  <div class="modal fade" id="SelectTypeModal" tabindex="-1" aria-labelledby="SelectTypeModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-4">
          <h5 class="modal-title fs-24" id="SelectTypeModalLabel">Select Type</h5>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body">
          <button type="button" class="d-flex w-100 text-start border rounded bg-grey hover-grey mb-3 p-3"
                  data-bs-toggle="modal" data-bs-target="#EnterPriceModal">
            <i class="fa-regular fa-tag fs-20 mt-1"></i>
            <div class="__body ms-3">
              <h5 class="fs-18 mb-2">Fixed Price</h5>
              <p class="color-grey fs-14 mb-0">
                Enter a price so users can instantly buy your NFT
              </p>
            </div>
            <i class="fa-regular fa-angle-right my-auto ms-auto"></i>
          </button>
          <button type="button" class="d-flex w-100 text-start border rounded bg-grey hover-grey p-3"
                  data-bs-toggle="modal" data-bs-target="#PutAuctionModal">
            <i class="fa-regular fa-gavel fs-20 mt-1"></i>
            <div class="__body ms-3">
              <h5 class="fs-18 mb-2">Auction</h5>
              <p class="color-grey fs-14 mb-0">
                Allow other users to bet on your NFT
              </p>
            </div>
            <i class="fa-regular fa-angle-right my-auto ms-auto"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "SelectTypeModal"
}
</script>

<style scoped>

</style>
