<template>
  <div class="card-item-details border bg-soft p-4 rounded">
    <h4 class="fs-18 mb-4">Details</h4>
    <ul class="list-unstyled">
      <li class="mb-3">
        <a :href="itemExplorerLink"
           class="d-flex align-items-center p-3 rounded border hover text-white"
           target="_blank">
          <span class="fw-medium color-grey">Contract Address</span>
          <span
              class="col-4 text-truncate ms-auto">{{ friendlyItemAddress }}</span>
          <i class="fa-solid fa-angle-right color-grey ms-3"></i>
        </a>
      </li>
      <li v-if="item.isOnSale" class="mb-3">
        <a :href="saleExplorerLink"
           class="d-flex align-items-center p-3 rounded border hover text-white"
           target="_blank">
          <span class="fw-medium color-grey">Sale Contract</span>
          <span
              class="col-4 text-truncate ms-auto">{{ friendlySaleAddress }}</span>
          <i class="fa-solid fa-angle-right color-grey ms-3"></i>
        </a>
      </li>
      <!-- TODO: An actual way to get item metadata status -->
      <li class="d-flex align-items-center p-3 rounded border hover text-white mb-3">
        <span class="fw-medium color-grey">Metadata</span>
        <span class="text-truncate ms-auto">Centralized</span>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import gql from "graphql-tag";
import {defineComponent} from "vue";
import {explorerLink, toFriendly} from "../../utility";

export default defineComponent({
  name: "ItemDetailsCard",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  apollo: {
    item: {
      query: gql`query item($address: String!) {
        item(address: $address) {
          isOnSale
          saleAddress
        }
      }`,
      variables() {
        return {
          address: this.address
        }
      }
    }
  },
  data() {
    return {
      item: {
        isOnSale: false,
        saleAddress: null as string | null,
      }
    }
  },
  computed: {
    itemExplorerLink() {
      return explorerLink(this.address)
    },
    friendlyItemAddress() {
      return toFriendly(this.address)
    },
    saleExplorerLink() {
      if (this.item.saleAddress)
        return explorerLink(this.item.saleAddress)
      else
        return null
    },
    friendlySaleAddress() {
      if (this.item.saleAddress)
        return toFriendly(this.item.saleAddress)
      else
        return null
    }
  }
})
</script>
