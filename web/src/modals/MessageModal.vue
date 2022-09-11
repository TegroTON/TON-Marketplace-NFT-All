<template>
  <div class="modal fade" id="MessageModal" tabindex="-1" aria-labelledby="MessageModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-4">
          <h5 class="modal-title" id="MessageModalLabel">New message</h5>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body">
          <form>
            <div class="mb-3">
              <label for="recipient-name" class="col-form-label">Recipient:</label>
              <input type="text" class="form-control bg-soft" id="recipient-name" value="@Tegro">
            </div>
            <div class="mb-4">
              <label for="message-text" class="col-form-label">Message:</label>
              <textarea class="form-control bg-soft" id="message-text"></textarea>
            </div>
          </form>
          <button type="button" class="btn btn-sm btn-primary float-end">Send message</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MessageModal"
}
</script>

<style scoped>

</style>
