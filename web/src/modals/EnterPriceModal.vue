<template>
  <div class="modal fade" id="EnterPriceModal" tabindex="-1" aria-labelledby="EnterPriceModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-4">
          <h5 class="modal-title fs-24" id="SelectTypeModalLabel">Enter Price</h5>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body">
          <form action="">
            <label for="" class="mb-1">Price</label>
            <div class="input-group mb-4">
              <input type="text" class="form-control" value="10" placeholder="Enter Price">
              <span class="input-group-text" id="basic-addon1">TON</span>
            </div>
            <ul class="list-unstyled mb-4 pb-4 border-bottom">
              <li class="d-flex align-items-center mb-3">
                     <span class="color-grey">
                     Service fee
                     <a href="#!" data-bs-toggle="tooltip" data-bs-placement="right"
                        title="The commission that the marketplace takes when selling NFTs"><i
                         class="fa-regular fa-circle-info ms-2"></i></a>
                     </span>
                <span class="ms-auto">0.5 TON</span>
              </li>
              <li class="d-flex align-items-center">
                     <span class="color-grey">
                     Royalties
                     <a href="#!" data-bs-toggle="tooltip" data-bs-placement="right"
                        title="Percentage of the NFT value that the author receives on each resale"><i
                         class="fa-regular fa-circle-info ms-2"></i></a>
                     </span>
                <span class="ms-auto">0.5 TON</span>
              </li>
            </ul>
            <div class="d-flex align-items-center fw-medium mb-5">
              <span>You'll get</span>
              <span class="ms-auto">9 TON</span>
            </div>
            <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal"
                    data-bs-target="#CreateContractModal">Put up for sale
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "EnterPriceModal"
}
</script>

<style scoped>

</style>
