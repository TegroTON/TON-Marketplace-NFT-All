<template>
  <router-link :to="link" class="card-item-details d-block border bg-soft hover px-4 py-3 rounded text-white">
    <h4 class="fs-14 color-grey mb-4">Collection</h4>
    <div class="d-flex align-items-center">
      <img :src="image" alt="" class="img-fluid rounded-circle" width="40">
      <h4 class="collection__name fs-16 mb-0 ms-3"> {{ name }} </h4>
      <i class="fa-solid fa-angle-right color-grey ms-auto"></i>
    </div>
  </router-link>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {useItemCollectionQuery} from "../graphql/generated";
import defaultImage from "../assets/user-1.svg";

export default defineComponent({
  name: "ItemCollection",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  setup(props) {
    const response = useItemCollectionQuery({variables: {address: props.address}})

    return {
      data: response.data,
    }
  },
  computed: {
    isInCollection() {
      return this.data?.item?.collection != null
    },
    link() {
      if (this.isInCollection) {
        return {name: 'collection', params: {address: this.data?.item?.collection?.address}}
      } else {
        return {name: 'explore'}
      }
    },
    name() {
      if (this.isInCollection) {
        return this.data?.item?.collection?.name ?? 'Untitled Collection'
      } else {
        return 'Not In Collection'
      }
    },
    image() {
      return this.data?.item?.collection?.image ?? defaultImage
    }
  }
})
</script>
