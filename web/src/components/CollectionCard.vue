<template>
  <div class="card card-gradient top-effect">
    <router-link :to="{name: 'collection', params: {address: address}}" class="card-link">
      <picture>
        <img :alt="collection.metadata.name" :src="collection.metadata.image" class="image-300x300"
             height="275" loading="lazy" width="340">
      </picture>
      <div class="card-body">
        <div class="card-avatar">
          <img :alt="collection.metadata.name" :src="collection.metadata.image" class="img-fluid" height="80"
               loading="lazy"
               width="80">
        </div>
        <div class="d-flex flex-wrap align-items-center">
          <div class="me-auto mt-3">
            <h3 class="fs-20 d-flex align-items-center">
              <span class="collections__card-name">{{ collection.metadata.name }}</span>
              <i class="fa-solid fa-circle-check fs-16 color-yellow ms-2"></i>
            </h3>
            <p v-if="false" class="mb-0 color-grey">
              6.7K owners
            </p>
          </div>
          <div class="d-flex align-items-center mt-3">
            <div class="pe-3">
              <p class="fw-medium text-white mb-2">{{ collection.contract.size }}</p>
              <p class="m-0 color-grey">Items</p>
            </div>
            <div v-if="false" class="pe-3">
              <p class="fw-medium text-white mb-2">167.8K eTH</p>
              <p class="m-0 color-grey">Total volume</p>
            </div>
            <div v-if="false">
              <p class="fw-medium text-white mb-2">16.48 eTH</p>
              <p class="m-0 color-grey">floor</p>
            </div>
          </div>
        </div>
      </div>
    </router-link>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";

export default defineComponent({
  name: "CollectionCard",
  props: {
    address: {
      type: String,
      required: true,
    },
  },
  apollo: {
    collection: {
      query: gql`query collection($address: String!) {
        collection(address: $address) {
          contract {
            size
          }
          metadata {
            name
            image
          }
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
        contract: {
          size: 0,
        },
        metadata: {
          name: "Loading..." as string | null,
          image: "" as string | null,
        },
      }
    }
  },
})
</script>
