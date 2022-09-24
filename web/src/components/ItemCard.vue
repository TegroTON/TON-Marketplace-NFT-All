<template>
  <div class="card bg-transparent border">
    <router-link :to="link" class="card-link">
      <picture>
        <img :alt="displayName" :src="image" class="card-image img-fluid" height="250" loading="lazy" width="276">
      </picture>
      <div class="card-body px-1 py-0">
        <h4 class="fs-18 d-flex align-items-center my-4">
          <span class="icon-ton me-2"></span> {{ displayName }}
        </h4>
        <div class="d-flex justify-content-between bg-soft rounded p-3 fs-14">
          <div v-if="isOnSale">
            <p class="mb-1 fw-medium color-grey">Price</p>
            <p class="m-0 text-white">{{ formattedFullPrice }}</p>
          </div>
          <div v-else>
            <p class="mb-1 fw-medium color-grey">Not On Sale</p>
          </div>
        </div>
      </div>
    </router-link>
    <button
        class="btn btn-sm btn-details__like d-flex align-items-center bg-soft blur-filter py-2 px-3 position-absolute mt-2 ms-2"
        type="button">
      <i class="fa-regular fa-heart m-0 me-sm-2 fs-16"></i>
      <span class="d-block d-sm-inline mt-1 mt-sm-0 fs-14">24</span>
    </button>
    <div v-if="isOnSale" class="card__show-effect">
      <button class="btn btn-sm bg-white text-dark py-2" data-bs-target="#BuyNowModal" data-bs-toggle="modal"
              type="button">Buy Now
      </button>
    </div>
    <div :style="{background: 'url(' + image + ')  no-repeat center center / cover'}"
         class="card__blur-bg-hover"></div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {useItemCardQuery} from "../graphql/generated";
import defaultImage from "../assets/user-1.svg";
import {formatPrice} from "../utility";

export default defineComponent({
  name: "ItemCard",
  props: {
    address: {
      type: String,
      required: true
    }
  },
  setup(props) {
    const response = useItemCardQuery({variables: {address: props.address}})

    return {
      data: response.data
    }
  },
  computed: {
    link() {
      return {name: 'item', params: {address: this.address}}
    },
    image() {
      return this.data?.item?.image ?? defaultImage
    },
    displayName() {
      return this.data?.item?.name ?? ("Item no. " + this.data?.item?.index ?? '??')
    },
    isOnSale() {
      return this.data?.item?.sale != null
    },
    formattedFullPrice() {
      if (this.data?.item?.sale?.fullPrice != null) {
        return formatPrice(this.data?.item?.sale?.fullPrice)
      } else {
        return null
      }
    }
  },
})
</script>

<style lang="scss" scoped>

</style>
