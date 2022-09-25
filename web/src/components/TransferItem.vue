<template>
  <button class="btn btn-soft flex-fill m-2" data-bs-target="#TransferModal" data-bs-toggle="modal" type="button">
    Transfer
  </button>

  <teleport to="#modals">
    <div id="TransferModal" aria-hidden="true" aria-labelledby="EnterPriceModalLabel" class="modal fade" tabindex="-1">
      <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
        <div v-if="!isComplete" class="modal-content border-0">
          <div class="modal-header border-0 mb-4">
            <h5 id="SelectTypeModalLabel" class="modal-title fs-24">Transfer ownership</h5>
            <button aria-label="Close" class="border-0 p-0 modal-close" data-bs-dismiss="modal" type="button">
              <i class="fa-solid fa-xmark fa-lg"></i>
            </button>
          </div>
          <div class="modal-body">
            <form action="">
              <label class="mb-1" for="">New owner address</label>
              <div class="input-group mb-4">
                <input v-model="destination" :class="isInvalidDestination ? 'is-invalid' : 'is-valid'"
                       class="form-control"
                       placeholder="Enter Address" type="text" @change="checkDestinationValidity()">
              </div>
              <ul class="list-unstyled mb-4 pb-4 border-bottom">
                <li class="d-flex align-items-center mb-3">
                     <span class="color-grey">
                     Forward amount
                     <a data-bs-placement="right" data-bs-toggle="tooltip" href="#!"
                        title="Amount sent to the new owner with confirming change of the ownership">
                       <i class="fa-regular fa-circle-info ms-2"></i>
                     </a>
                     </span>
                  <span class="ms-auto">0.05 TON</span>
                </li>
                <li class="d-flex align-items-center">
                     <span class="color-grey">
                     Network fee
                     <a data-bs-placement="right" data-bs-toggle="tooltip" href="#!"
                        title="Worst-case amount deduced by the network to pay for transaction processing">
                       <i class="fa-regular fa-circle-info ms-2"></i>
                     </a>
                     </span>
                  <span class="ms-auto">0.05 TON</span>
                </li>
              </ul>
              <div class="d-flex align-items-center fw-medium mb-5">
                <span>You'll pay</span>
                <span class="ms-auto">0.1 TON</span>
              </div>
              <button :disabled="isInvalidDestination" class="btn btn-primary w-100"
                      type="button"
                      @click="requestTransfer()">
                Transfer Ownership
                <div v-if="isInProgress" class="spinner-border spinner-border-sm" role="status"></div>
              </button>
            </form>
          </div>
        </div>
        <div v-else class="modal-content border-0">
          <div class="modal-header border-0 mb-4">
            <h5 id="SelectTypeModalLabel" class="modal-title fs-24">Transfer ownership</h5>
            <button aria-label="Close" class="border-0 p-0 modal-close" data-bs-dismiss="modal" type="button">
              <i class="fa-solid fa-xmark fa-lg"></i>
            </button>
          </div>
          <div class="modal-body">
            <p>
              Success! You may close this window now
            </p>
          </div>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script lang="ts">
import {defineComponent, ref} from "vue";
import {useConnectionStore} from "../stores/ConnectionStore";
import {useTransferItemQuery} from "../graphql/generated";
import {Address} from "ton";

export default defineComponent({
  name: "TransferItem",
  props: {
    item: {
      type: String,
      required: true,
    }
  },
  setup(props) {
    const connectionStore = useConnectionStore()
    const destination = ref(null as null | string)
    const state = ref('invalid' as 'invalid' | 'valid' | 'in_progress' | 'complete')

    const response = useTransferItemQuery({
      pause: state == 'invalid', // Wait for a valid address
      variables: {
        address: props.item,
        newOwner: destination!!!,
        responseDestination: connectionStore.walletAddress,
      }
    })

    return {
      connectionStore: connectionStore,
      data: response.data,
      destination: destination,
      state: state,
    }
  },
  computed: {
    isInvalidDestination() {
      return this.state == 'invalid'
    },
    isInProgress() {
      return this.state == 'in_progress'
    },
    isComplete() {
      return this.state == 'complete'
    }
  },
  methods: {
    checkDestinationValidity() {
      try {
        if (this.destination != null) {
          Address.parse(this.destination)
          this.state = 'valid'
          return
        }
      } catch {
      }
      this.state = 'invalid'
    },
    requestTransfer() {
      if (this.state == 'valid' && this.data?.item?.transfer != null) {
        this.state = 'in_progress'
        this.connectionStore.requestTransaction(this.data?.item?.transfer)
            .then(() => {
              this.state = 'complete'
            })
      }
    }
  }
})
</script>

<style lang="scss" scoped>

</style>
