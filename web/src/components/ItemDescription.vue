<template>
  <div class="card-item-details mb-5">
    <div v-if="isOnSale" class="item-details__badge badge__green mb-4">For Sale</div>
    <div class="item-details__badge mb-4">Not for sale</div>
    <h1 class="item-details__title mb-3">{{ displayName }}</h1>
    <p class="item-details__desc mb-0"> {{ description }} </p>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {useItemDescriptionQuery} from "../graphql/generated";

export default defineComponent({
  name: "ItemDescription",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  setup(props) {
    const response = useItemDescriptionQuery({variables: {address: props.address}})

    return {
      data: response.data
    }
  },
  computed: {
    isOnSale() {
      return this.data?.item?.sale != null
    },
    displayName() {
      return this.data?.item?.name ?? ("Item no. " + this.data?.item?.index ?? '??')
    },
    description() {
      return this.data?.item?.description ?? ''
    }
  }
})
</script>

<style lang="scss" scoped>

</style>
