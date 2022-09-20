<template>
  <div class="col-xl-6 mb-4">
    <router-link :to="linkDestination"
                 class="card-item-details d-block border bg-soft hover px-4 py-3 rounded text-white">
      <h4 class="fs-14 color-grey mb-4">Collection</h4>
      <div class="d-flex align-items-center">
        <img :src="collectionImage" alt="" class="img-fluid rounded-circle"
             width="40">
        <h4 class="collection__name fs-16 mb-0 ms-3"> {{ collectionName }} </h4>
        <i class="fa-solid fa-angle-right color-grey ms-auto"></i>
      </div>
    </router-link>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";
import defaultImage from "../../assets/img/user-1.svg";

export default defineComponent({
  name: "TheItemCollectionCard",
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
          collection {
            address
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
      item: {
        collection: null as {
          address: string,
          name: null | string,
          image: null | string,
        } | null,
      }
    }
  },
  computed: {
    linkDestination() {
      if (this.item.collection !== null) {
        return {name: 'collection', params: {address: this.item.collection.address}}
      } else {
        return {name: 'explore'}
      }
    },
    collectionImage() {
      if (this.item.collection !== null && this.item.collection?.image !== null) {
        return this.item.collection.image
      } else {
        return defaultImage
      }
    },
    collectionName() {
      if (this.item.collection !== null) {
        return this.item.collection.name ?? "Untitled Collection"
      } else {
        return "Not In Collection"
      }
    }
  }
})
</script>
