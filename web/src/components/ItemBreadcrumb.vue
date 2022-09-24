<template>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb mb-5 ms-3">
      <li class="breadcrumb-item">
        <router-link :to="{name: 'explore'}">Explore</router-link>
      </li>
      <li v-if="isInCollection" class="breadcrumb-item">
        <router-link :to="collectionLink">{{ collectionName }}</router-link>
      </li>
      <li aria-current="page" class="breadcrumb-item active">{{ itemDisplayName }}</li>
    </ol>
  </nav>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {useItemBreadcrumbQuery} from "../graphql/generated";

export default defineComponent({
  name: "ItemBreadcrumb",
  props: {
    address: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const response = useItemBreadcrumbQuery({variables: {address: props.address}})

    return {
      data: response.data,
    }
  },
  computed: {
    isInCollection() {
      return this.data?.item?.collection != null
    },
    collectionLink() {
      return {name: 'collection', params: {address: this.data?.item?.collection?.address}}
    },
    collectionName() {
      return this.data?.item?.collection?.name ?? 'Untitled Collection'
    },
    itemDisplayName() {
      return this.data?.item?.name ?? ("Item no. " + this.data?.item?.index ?? '??')
    },
  },
})
</script>

<style lang="scss" scoped>

</style>
