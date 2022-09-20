<template>
  <div class="card bg-transparent border">
    <router-link :to="{name: 'item', params: {address: address}}" class="card-link">
      <picture>
        <img :alt="item.name ?? 'Item Content'" :src="item.image ?? 'assets/img/cats/t-cat-01.jpg'"
             class="card-image img-fluid" height="250" loading="lazy" width="276">
      </picture>
      <div class="card-body px-1 py-0">
        <h4 class="fs-18 d-flex align-items-center my-4">
          <span class="icon-ton me-2"></span> {{ item.name ?? 'Item no. ' + item.contract.index }}
        </h4>
        <div class="d-flex justify-content-between bg-soft rounded p-3 fs-14">
          <div v-if="item.isOnSale">
            <p class="mb-1 fw-medium color-grey">Price</p>
            <p class="m-0 text-white">{{ formattedPrice }}</p>
          </div>
          <div v-else>
            <p class="m-0 text-white">Not For Sale</p>
          </div>
          <!--          <div>-->
          <!--            <p class="mb-1 fw-medium color-grey">Highest bid</p>-->
          <!--            <p class="m-0 text-white text-end">164 TON</p>-->
          <!--          </div>-->
        </div>
      </div>
    </router-link>
    <button v-if="false"
            class="btn btn-sm btn-details__like d-flex align-items-center bg-soft blur-filter py-2 px-3 position-absolute mt-2 ms-2"
            type="button">
      <i class="fa-regular fa-heart m-0 me-sm-2 fs-16"></i>
      <span class="d-block d-sm-inline mt-1 mt-sm-0 fs-14">24</span>
    </button>
    <div v-if="item.isOnSale" class="card__show-effect">
      <button v-if="false" class="btn btn-sm bg-white text-dark py-2" data-bs-target="#BuyNowModal"
              data-bs-toggle="modal"
              type="button">Buy Now
      </button>
    </div>
    <div
        :style="'background: url(' + (item.image ?? 'assets/img/cats/t-cat-01.jpg') + ')  no-repeat center center / cover'"
        class="card__blur-bg-hover"></div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";
import {fromNano} from "ton";

export default defineComponent({
  name: "CollectionItemCard",
  props: {
    address: {
      type: String,
      required: true,
    },
  },
  apollo: {
    item: {
      query: gql`query collectionItem($address: String!) {
        item(address: $address) {
          address
          index
          name
          image
          isOnSale
          fullPrice
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
        index: "0",
        name: "Loading..." as string | null,
        image: "" as string | null,
        isOnSale: false,
        fullPrice: "0"
      }
    }
  },
  computed: {
    formattedPrice: function () {
      return fromNano(this.item.fullPrice) + " TON"
    }
  },
})
</script>

<style scoped>

</style>
