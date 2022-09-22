<template>
  <div class="card card-blur p-0 mt--100 mb-4">
    <div v-if="false" class="card__share d-flex position-absolute" style="top: 8px; right: 8px">
      <button class="btn btn-cube" type="button"><i class="fa-regular fa-share-nodes"></i></button>
      <div class="dropdown">
        <button id="dropdownMenuButton1" aria-expanded="false" class="btn btn-cube" data-bs-toggle="dropdown"
                type="button">
          <i class="fa-regular fa-ellipsis-stroke"></i>
        </button>
        <ul aria-labelledby="dropdownMenuButton1" class="dropdown-menu">
          <li><a class="dropdown-item border-0" href="#">Report page</a></li>
        </ul>
      </div>
    </div>
    <div class="card-body p-4 p-lg-2 p-xl-3 p-xxl-4">
      <div class="d-flex flex-column flex-md-row flex-lg-column align-items-center mb-5">
        <div class="collection__image mb-4 mb-md-0 mb-lg-4 ms-auto ms-md-0 ms-lg-auto me-auto">
          <img :src="collection.image" alt="" class="img-fluid rounded-circle">
        </div>
        <div v-if="false" class="d-flex mx-0 mx-lg-auto">
          <a class="btn btn-sm btn-outline-primary" href="#!">Subscribe</a>
        </div>
      </div>
      <h1 class="collection__name fs-24 mb-4">
        <span>{{ collectionDisplayName }}</span>
        <i class="fa-solid fa-circle-check fs-22 color-yellow ms-2"></i>
      </h1>
      <div class="collection__desc color-grey mb-4">
        <p>{{ collection.description }}</p>

        <a v-if="false" class="collection__link text-white" href="#!" target="__blank">
          <i class="fa-regular fa-link-simple color-yellow me-2"></i>
          boredapeyachtclub.com
        </a>
      </div>
      <div v-if="false" class="libermall__soclinks">
        <a class="libermall__soclinks-item ms-0" href="#!"><i class="fa-brands fa-telegram"></i></a>
        <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-discord"></i></a>
        <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-instagram"></i></a>
        <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-linkedin-in"></i></a>
        <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-reddit-alien"></i></a>
      </div>
    </div>
    <div class="card-footer text-center border-top px-5 py-3">
      Created by <span class="color-yellow">{{ ownerDisplayAddress }}</span>
      <i class="fa-solid fa-circle-check color-yellow fs-14 ms-1"></i>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";
import {normalizeAndShorten} from "../../utility";

export default defineComponent({
  name: "CollectionInfoCard",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  apollo: {
    collection: {
      query: gql`query collectionInfo($address: String!) {
        collection(address: $address) {
          address
          owner
          name
          description
          image
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
        owner: null as string | null,
        name: null as string | null,
        description: null as string | null,
        image: null as string | null,
      }
    }
  },
  computed: {
    collectionDisplayName() {
      return this.collection.name ?? "Untitled Collection"
    },
    ownerDisplayAddress() {
      if (this.collection.owner !== null)
        return normalizeAndShorten(this.collection.owner)
      else
        return "unknown"
    },
  }
})
</script>

<style scoped>

</style>
