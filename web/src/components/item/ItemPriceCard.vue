<template>
  <div v-if="item.isOnSale" class="card-item-details border bg-soft p-4 rounded mb-4">
    <div class="d-flex align-items-center mb-2">
      <span class="d-block text-uppercase fs-18 fw-medium">Price:</span>
      <span class="price-item__ton d-block ms-auto">{{ formattedPrice }}</span>
    </div>
    <div class="d-flex align-items-center mb-4">
      <span class="color-grey">Plus a network fee of 1 TON</span>
      <!-- TODO: USD price -->
      <span v-if="false" class="price-item__dollar d-block color-grey text-end ms-auto">$64.09</span>
    </div>
    <button class="btn btn-primary w-100" data-bs-target="#BuyNowModal" data-bs-toggle="modal" type="button">
      Buy Now
    </button>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";
import {formatPrice} from "../../utility";

export default defineComponent({
  name: "ItemPriceCard",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  apollo: {
    item: {
      query: gql`query itemPrice($address: String!) {
        item(address: $address) {
          address
          isOnSale
          fullPrice
          networkFee
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
        address: this.address,
        isOnSale: false,
        fullPrice: "0",
        networkFee: "0",
      }
    }
  },
  computed: {
    formattedPrice() {
      return formatPrice(this.item.fullPrice)
    },
    formattedNetworkFee() {
      return formatPrice(this.item.networkFee)
    }
  },
})
</script>

<style scoped>

</style>
