<template>
  <div v-if="isOnSale" class="card-item-details border bg-soft p-4 rounded mb-4">
    <div class="d-flex align-items-center mb-2">
      <span class="d-block text-uppercase fs-18 fw-medium">Price:</span>
      <span class="price-item__ton d-block ms-auto">{{ formattedFullPrice }}</span>
    </div>
    <div class="d-flex align-items-center mb-4">
      <span class="color-grey">Plus a network fee of {{ formattedNetworkFee }}</span>
      <span v-if="false" class="price-item__dollar d-block color-grey text-end ms-auto">$64.09</span>
    </div>
    <button v-if="!isOwnedByUser" class="btn btn-primary w-100" data-bs-target="#BuyNowModal" data-bs-toggle="modal"
            type="button">Buy
      Now
    </button>
  </div>
  <div v-if="isOwnedByUser" class="card-item-details d-flex flex-wrap border bg-soft p-2 rounded mb-4">
    <button class="btn btn-primary flex-fill m-2" data-bs-target="#SelectTypeModal" data-bs-toggle="modal"
            type="button">Put up for sale
    </button>
    <transfer-item :item="address"></transfer-item>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {useItemPriceQuery} from "../graphql/generated";
import {formatPrice, toFriendly} from "../utility";
import {useConnectionStore} from "../stores/ConnectionStore";
import TransferItem from "./TransferItem.vue";

export default defineComponent({
  name: "ItemPrice",
  components: {TransferItem},
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  setup(props) {
    const connectionStore = useConnectionStore()
    const response = useItemPriceQuery({variables: {address: props.address}})

    return {
      data: response.data,
      connectionStore: connectionStore,
    }
  },
  computed: {
    isOnSale() {
      return this.data?.item?.sale != null
    },
    formattedFullPrice() {
      if (this.data?.item?.sale?.fullPrice != null) {
        return formatPrice(this.data?.item?.sale?.fullPrice)
      } else {
        return ''
      }
    },
    formattedNetworkFee() {
      if (this.data?.item?.sale?.networkFee != null) {
        return formatPrice(this.data?.item?.sale?.networkFee)
      } else {
        return '1 TON'
      }
    },
    isOwnedByUser() {
      if (this.data?.item?.owner?.address != null && this.connectionStore.walletAddress != null) {
        return toFriendly(this.data?.item?.owner?.address) == toFriendly(this.connectionStore.walletAddress)
      } else {
        return false
      }
    }
  },
})
</script>
