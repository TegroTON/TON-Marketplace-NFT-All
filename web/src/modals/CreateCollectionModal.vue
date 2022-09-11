<template>
  <div class="modal fade" id="CreateCollectionModal" tabindex="-1" aria-labelledby="CreateCollectionModalLabel"
       aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-4">
          <h5 class="modal-title fs-24" id="SelectTypeModalLabel">Create Collection ERC-721</h5>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <form class="modal-body">
          <div class="d-flex mb-5">
            <div class="me-3">
              <img src="assets/img/user-1.svg" class="img-fluid rounded" alt="" width="120" height="120">
            </div>
            <div>
              <p class="color-grey">
                We recommend an image of at least 300x300. Gifs work too. Max 5mb.
              </p>
              <div>
                <label for="formFile" class="btn btn-sm btn-soft">Choose file</label>
                <input class="form-control d-none" type="file" id="formFile">
              </div>
            </div>
          </div>
          <div class="mb-4">
            <h4 class="fs-16 mb-3">Display name <span class="color-grey fs-14 ms-1">(required)</span></h4>
            <input type="text" class="form-control mb-2" placeholder="Enter collection name" required="">
            <span class="fw-medium fs-14 color-grey px-1">Token name cannot be changed in future</span>
          </div>
          <div class="mb-4">
            <h4 class="fs-16 mb-3">Symbol <span class="color-grey fs-14 ms-1">(required)</span></h4>
            <input type="text" class="form-control" placeholder="Enter token symbol" required="">
          </div>
          <div class="mb-4">
            <h4 class="fs-16 mb-3">Description <span class="color-grey fs-14 ms-1">(Optional)</span></h4>
            <textarea class="form-control p-3" placeholder="Spread some words about your token collection"
                      style="height: 70px"></textarea>
          </div>
          <div class="mb-4">
            <h4 class="fs-16 mb-3">Short url</h4>
            <div class="input-group mb-2">
              <span class="input-group-text">libermall.com/</span>
              <input type="text" class="form-control" placeholder="Enter short url">
            </div>
            <span class="fw-medium fs-14 color-grey px-1">Will be used as public URL</span>
          </div>
          <button type="button" class="btn btn-sm btn-primary w-100">Create Collection</button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "CreateCollectionModal"
}
</script>

<style scoped>

</style>
