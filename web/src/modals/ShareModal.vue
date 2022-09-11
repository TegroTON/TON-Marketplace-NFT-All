<template>
  <div class="modal fade" id="ShareModal" tabindex="-1" aria-labelledby="ShareModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-5">
          <h5 class="modal-title fs-24" id="SelectTypeModalLabel">Share</h5>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body">
          <div class="libermall__soclinks justify-content-center p-2 border rounded">
            <a href="#!" class="libermall__soclinks-item ms-0"><i class="fa-brands fa-telegram"></i></a>
            <a href="#!" class="libermall__soclinks-item"><i class="fa-brands fa-discord"></i></a>
            <a href="#!" class="libermall__soclinks-item"><i class="fa-brands fa-instagram"></i></a>
            <a href="#!" class="libermall__soclinks-item"><i class="fa-brands fa-linkedin-in"></i></a>
            <a href="#!" class="libermall__soclinks-item"><i class="fa-brands fa-reddit-alien"></i></a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ShareModal"
}
</script>

<style scoped>

</style>
