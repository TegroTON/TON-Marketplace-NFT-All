<template>
  <entity-info>
    <template #image>
      <img :src="collectionImage" alt="" class="img-fluid rounded-circle">
    </template>
    <template #actions>
      <a class="btn btn-sm btn-outline-primary" href="#!">Subscribe</a>
    </template>
    <template #name>
      <span>{{ collectionDisplayName }}</span>
      <i class="fa-solid fa-circle-check fs-22 color-yellow ms-2"></i>
    </template>
    <template #description>
      <p>
        {{ collectionDescription }}
      </p>
    </template>
    <template v-if="hasAnOwner" #footer>
      Created by <span class="color-yellow">{{ formattedOwnerAddress }}</span>
      <i class="fa-solid fa-circle-check color-yellow fs-14 ms-1"></i>
    </template>
  </entity-info>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {useCollectionInfoQuery} from "../graphql/generated";
import EntityInfo from "./EntityInfo.vue";
import defaultImage from "../assets/user-1.svg";
import {normalizeAndShorten} from "../utility";

export default defineComponent({
  name: "CollectionInfo",
  components: {EntityInfo},
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  setup(props) {
    const response = useCollectionInfoQuery({variables: {address: props.address}})

    return {
      data: response.data
    }
  },
  computed: {
    collectionImage() {
      return this.data?.collection?.image ?? defaultImage
    },
    collectionDisplayName() {
      return this.data?.collection?.name ?? 'Untitled Collection'
    },
    collectionDescription() {
      return this.data?.collection?.description ?? ''
    },
    hasAnOwner() {
      return this.data?.collection?.owner != null
    },
    formattedOwnerAddress() {
      if (this.data?.collection?.owner != null) {
        return normalizeAndShorten(this.data?.collection?.owner?.address)
      } else {
        return ''
      }
    }
  }
})
</script>

<style lang="scss" scoped>

</style>
