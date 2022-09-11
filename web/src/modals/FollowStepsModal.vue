<template>
  <div class="modal fade" id="FollowStepsModal" tabindex="-1" aria-labelledby="FollowStepsModalLabel"
       aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-5">
          <h5 class="modal-title fs-24" id="SelectTypeModalLabel">Follow Steps</h5>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body">
          <ul class="list-unstyled mb-5">
            <li class="d-flex mb-4">
              <i class="fa-duotone fa-spinner-third fa-2x color-yellow"></i>
              <div class="ms-3">
                <h5 class="fs-16">Upload</h5>
                <p class="mb-0 color-grey">
                  Uploading off all media assets and metadata to IPFS
                </p>
              </div>
            </li>
            <li class="d-flex mb-4">
              <i class="fa-regular fa-circle-check fa-2x color-yellow"></i>
              <div class="ms-3">
                <h5 class="fs-16">Mint</h5>
                <p class="mb-0 color-grey">
                  Send transaction to create your NFT
                </p>
              </div>
            </li>
            <li class="d-flex">
              <i class="fa-regular fa-circle-check fa-2x color-yellow"></i>
              <div class="ms-3">
                <h5 class="fs-16">Start auction</h5>
                <p class="mb-0 color-grey">
                  Sign message to create timed auction
                </p>
              </div>
            </li>
          </ul>
          <button class="btn btn-soft w-100" data-bs-toggle="modal" data-bs-target="#CreatNFTSuccessfullyModal">Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "FollowStepsModal"
}
</script>

<style scoped>

</style>
