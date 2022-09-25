<template>
  <div class="card-item-details border bg-soft p-4 rounded">
    <h4 class="fs-18 mb-4">Details</h4>
    <ul class="list-unstyled">
      <li class="mb-3">
        <a :href="exploreContract"
           class="d-flex align-items-center p-3 rounded border hover text-white" target="_blank">
          <span class="fw-medium color-grey">Contract Address</span>
          <span class="col-4 text-truncate ms-auto">{{ contractAddress }}</span>
          <i class="fa-solid fa-angle-right color-grey ms-3"></i>
        </a>
      </li>
      <li v-if="isOnSale" class="mb-3">
        <a :href="exploreSale"
           class="d-flex align-items-center p-3 rounded border hover text-white" target="_blank">
          <span class="fw-medium color-grey">Sale Contract</span>
          <span class="col-4 text-truncate ms-auto">{{ saleAddress }}</span>
          <i class="fa-solid fa-angle-right color-grey ms-3"></i>
        </a>
      </li>
      <li v-if="hasRoyalty" class="mb-3">
        <a :href="exploreRoyalty"
           class="d-flex align-items-center p-3 rounded border hover text-white" target="_blank">
          <span class="fw-medium color-grey">Royalty Holder</span>
          <span class="col-4 text-truncate ms-auto">{{ royaltyAddress }}</span>
          <i class="fa-solid fa-angle-right color-grey ms-3"></i>
        </a>
      </li>
      <li v-if="hasRoyalty" class="d-flex align-items-center p-3 rounded border hover text-white mb-3">
        <span class="fw-medium color-grey">Royalty</span>
        <span class="text-truncate ms-auto">{{ formattedRoyalty }}</span>
      </li>
      <li class="d-flex align-items-center p-3 rounded border hover text-white mb-3">
        <span class="fw-medium color-grey">Index</span>
        <span class="text-truncate ms-auto">{{ index }}</span>
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
import {useItemDetailsQuery} from "../graphql/generated";
import {explorerLink, toFriendly} from "../utility";

export default defineComponent({
  name: "ItemDetails",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  setup(props) {
    const response = useItemDetailsQuery({variables: {address: props.address}})

    return {
      data: response.data
    }
  },
  computed: {
    exploreContract() {
      return explorerLink(this.address)
    },
    contractAddress() {
      return toFriendly(this.address)
    },
    isOnSale() {
      return this.data?.item?.sale != null
    },
    exploreSale() {
      if (this.data?.item?.sale != null) {
        return explorerLink(this.data?.item?.sale.address)
      } else {
        return null
      }
    },
    saleAddress() {
      if (this.data?.item?.sale != null) {
        return toFriendly(this.data?.item?.sale.address)
      } else {
        return null
      }
    },
    hasRoyalty() {
      return this.data?.item?.royalty != null
    },
    exploreRoyalty() {
      if (this.data?.item?.royalty != null) {
        return explorerLink(this.data?.item?.royalty.destination)
      } else {
        return null
      }
    },
    royaltyAddress() {
      if (this.data?.item?.royalty != null) {
        return toFriendly(this.data?.item?.royalty.destination)
      } else {
        return null
      }
    },
    formattedRoyalty() {
      if (this.data?.item?.royalty != null) {
        return (this.data?.item?.royalty.value * 100).toFixed(2) + '%'
      } else {
        return null
      }
    },
    index() {
      return this.data?.item?.index ?? "0"
    }
  }
})
</script>
