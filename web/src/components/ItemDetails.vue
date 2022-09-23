<template>
  <div class="card-item-details border bg-soft p-4 rounded">
    <h4 class="fs-18 mb-4">Details</h4>
    <ul class="list-unstyled">
      <li class="mb-3">
        <a class="d-flex align-items-center p-3 rounded border hover text-white"
           href="https://tonscan.org/address/EQC_-u5FytW3WrGR_UQ_tjVFvFbcIanSh4nHjqP3ojIamkGP" target="_blank">
          <span class="fw-medium color-grey">Contract Address</span>
          <span class="col-4 text-truncate ms-auto">EQC_-u5FytW3WrGR_UQ_tjVFvFbcIanSh4nHjqP3ojIamkGP</span>
          <i class="fa-solid fa-angle-right color-grey ms-3"></i>
        </a>
      </li>
      <li class="mb-3">
        <a class="d-flex align-items-center p-3 rounded border hover text-white"
           href="https://tonscan.org/address/EQC_-u5FytW3WrGR_UQ_tjVFvFbcIanSh4nHjqP3ojIamkGP" target="_blank">
          <span class="fw-medium color-grey">Sale Contract</span>
          <span class="col-4 text-truncate ms-auto">EQC_-u5FytW3WrGR_UQ_tjVFvFbcIanSh4nHjqP3ojIamkGP</span>
          <i class="fa-solid fa-angle-right color-grey ms-3"></i>
        </a>
      </li>
      <li class="d-flex align-items-center p-3 rounded border hover text-white mb-3">
        <span class="fw-medium color-grey">Token ID</span>
        <span class="text-truncate ms-auto">10931</span>
      </li>
      <li class="d-flex align-items-center p-3 rounded border hover text-white mb-3">
        <span class="fw-medium color-grey">Metadata</span>
        <span class="text-truncate ms-auto">Centralized</span>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "ItemDetails",
})
</script>

<style lang="scss" scoped>

</style>
