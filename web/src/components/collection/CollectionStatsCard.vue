<template>
  <div id="mouseScroll" class="card card-blur mb-4 overflow-auto">
    <div class="d-flex align-items-center justify-content-between">
      <div v-if="false" class="card-blur__item p-4 border-end text-center">
        <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">floor</h5>
        <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">0.04 eTH</p>
      </div>
      <div v-if="false" class="card-blur__item p-4 border-end text-center">
        <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">volume</h5>
        <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">40.61 ETH</p>
      </div>
      <div class="card-blur__item p-4 border-end text-center">
        <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">Items</h5>
        <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">{{ collection.itemNumber }}</p>
      </div>
      <div class="card-blur__item p-4 border-end text-center">
        <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">Owners</h5>
        <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">{{ collection.ownerNumber }}</p>
      </div>
      <div class="card-blur__item p-4 border-end text-center">
        <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">Blockchain</h5>
        <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">TON</p>
      </div>
      <div class="card-blur__item p-4 text-center">
        <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">address</h5>
        <p class="m-0 fw-medium text-truncate" style="letter-spacing: 1px;"> {{ collectionDisplayAddress }}</p>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";
import {normalizeAndShorten} from "../../utility";

export default defineComponent({
  name: "CollectionStatsCard",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  apollo: {
    collection: {
      query: gql`query collectionStats($address: String!) {
        collection(address: $address) {
          address
          itemNumber
          ownerNumber
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
      collection: {
        address: this.address,
        itemNumber: "0",
        ownerNumber: "0",
      }
    }
  },
  computed: {
    collectionDisplayAddress() {
      return normalizeAndShorten(this.address)
    },
  }
})
</script>

<style scoped>

</style>
