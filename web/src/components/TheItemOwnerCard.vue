<template>
  <div class="col-xl-6 mx-auto mb-4">
    <!--                    TODO: Link-->
    <div class="card-item-details d-block border bg-soft hover px-4 py-3 rounded text-white">
      <h4 class="fs-14 color-grey mb-4">Owner</h4>
      <div class="d-flex align-items-center">
        <img alt="" class="img-fluid rounded-circle" height="40" src="assets/img/author/author-12.jpg" width="40">
        <h4 class="collection__name fs-16 mb-0 ms-3">
          {{ shortOwnerAddress }}
        </h4>
        <i v-if="false" class="fa-solid fa-angle-right color-grey ms-auto"></i>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";
import {normalizeAndShorten} from "../utility";

export default defineComponent({
  name: "TheItemOwnerCard",
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
          owner
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
        owner: null as string | null,
      }
    }
  },
  computed: {
    shortOwnerAddress() {
      if (this.item.owner !== null)
        return normalizeAndShorten(this.item.owner)
      else
        return null
    }
  }
})
</script>
