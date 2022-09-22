<template>
  <div class="card-item-details mb-5">
    <div v-if="item.isOnSale" class="item-details__badge badge__green mb-4">For Sale</div>
    <div v-else class="item-details__badge mb-4">Not for sale</div>
    <h1 class="item-details__title mb-3">{{ itemDisplayName }}</h1>
    <p class="item-details__desc mb-0"> {{ item.description }} </p>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";

export default defineComponent({
  name: "ItemDescriptionCard",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  apollo: {
    item: {
      query: gql`query itemDescription($address: String!) {
        item(address: $address) {
          address
          isOnSale
          index
          name
          description
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
        index: "0",
        name: null as null | string,
        description: null as null | string,
      }
    }
  },
  computed: {
    itemDisplayName: function () {
      return this.item.name ?? ("Item no. " + this.item.index)
    },
  }
})
</script>

<style scoped>

</style>
