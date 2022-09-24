<template>
  <img :src="image" alt="NFT Item Content" class="item-details__image" data-enlargable>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {useItemContentQuery} from "../graphql/generated";
import defaultImage from "../assets/user-1.svg";

export default defineComponent({
  name: "ItemContent",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  setup(props) {
    const response = useItemContentQuery({variables: {address: props.address}})

    return {
      data: response.data,
    }
  },
  computed: {
    image() {
      return this.data?.item?.image ?? defaultImage
    }
  },
})
</script>

<style lang="scss" scoped>

</style>
