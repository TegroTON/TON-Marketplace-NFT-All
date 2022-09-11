<template>
  <div class="modal fade" id="UpdateCoverModal" tabindex="-1" aria-labelledby="UpdateCoverModalLabel"
       aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-2">
          <h5 class="modal-title fs-24" id="SelectTypeModalLabel">Update Cover</h5>
        </div>
        <div class="modal-body">
          <p class="color-grey mb-5">
            Upload new cover for your page. We recommend to upload image in 1920x340 resolution. Max 15 mb.
          </p>
          <div class="mb-3">
            <label for="formFile" class="btn btn-sm btn-primary w-100">Choose file</label>
            <input class="form-control d-none" type="file" id="formFile">
          </div>
          <button type="button" class="btn btn-sm btn-soft w-100" data-bs-dismiss="modal" aria-label="Close">Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "UpdateCoverModal"
}
</script>

<style scoped>

</style>
