<template>
  <router-link :to="link" class="card-item-details d-block border bg-soft hover px-4 py-3 rounded text-white">
    <h4 class="fs-14 color-grey mb-4">Owner</h4>
    <div class="d-flex align-items-center">
      <img :src="image" alt="" class="img-fluid rounded-circle" height="40"
           width="40">
      <h4 class="collection__name fs-16 mb-0 ms-3"> {{ formattedAddress }} </h4>
      <i class="fa-solid fa-angle-right color-grey ms-auto"></i>
    </div>
  </router-link>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {useItemOwnerQuery} from "../graphql/generated";
import {normalizeAndShorten} from "../utility";
import defaultImage from "../assets/user-1.svg";

export default defineComponent({
  name: "ItemOwner",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  setup(props) {
    const response = useItemOwnerQuery({variables: {address: props.address}})

    return {
      data: response.data
    }
  },
  computed: {
    link() {
      return {name: 'profile', params: {address: this.data?.item?.owner?.address}}
    },
    formattedAddress() {
      if (this.data?.item?.owner?.address != null) {
        return normalizeAndShorten(this.data?.item?.owner?.address)
      } else {
        return 'Unknown'
      }
    },
    image() {
      return defaultImage
    }
  }
})
</script>
