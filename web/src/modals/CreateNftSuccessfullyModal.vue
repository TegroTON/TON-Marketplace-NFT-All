<template>
  <div class="modal fade" id="CreatNFTSuccessfullyModal" tabindex="-1" aria-labelledby="CreatNFTSuccessfullyModalLabel"
       aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-body">
          <div class="d-flex align-items-center bg-soft rounded p-2 mb-4">
            <img class="rounded image-80x80" src="assets/img/item-details-image.jpg" alt="Tergo Cat">
            <div class="__body ms-3">
              <h5 class="fs-18 mb-0">Your <span class="color-yellow">Tegro Cat #133 NFT</span> <br> successfully created
              </h5>
            </div>
          </div>
          <ul class="list-unstyled p-3 border rounded mb-5">
            <li class="d-flex mb-3">
              <span class="color-grey">Status</span>
              <span class="ms-auto">Transaction hash</span>
            </li>
            <li class="d-flex">
              <span class="color-green">Confirmed</span>
              <span class="d-block text-truncate ms-auto" style="max-width: 150px;">
                  EQDZr7KDKG0R4Kyauz-iRetnuY7nMKXFIEQn4-44vzygNEvj
                  </span>
            </li>
          </ul>
          <h4 class="fs-18 text-center mb-3">Let's show-off a little</h4>
          <div class="libermall__soclinks justify-content-center mb-5 p-2 border rounded">
            <a href="#!" class="libermall__soclinks-item ms-0"><i class="fa-brands fa-telegram"></i></a>
            <a href="#!" class="libermall__soclinks-item"><i class="fa-brands fa-discord"></i></a>
            <a href="#!" class="libermall__soclinks-item"><i class="fa-brands fa-instagram"></i></a>
            <a href="#!" class="libermall__soclinks-item"><i class="fa-brands fa-linkedin-in"></i></a>
            <a href="#!" class="libermall__soclinks-item"><i class="fa-brands fa-reddit-alien"></i></a>
          </div>
          <div>
            <button class="btn btn-primary w-100 mb-2" data-bs-dismiss="modal" aria-label="Close">View NFT</button>
            <button class="btn btn-soft w-100" data-bs-dismiss="modal" aria-label="Close">Create another</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "CreateNftSuccessfullyModal"
}
</script>

<style scoped>

</style>
