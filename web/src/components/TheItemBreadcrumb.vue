<template>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb mb-5 ms-3">
      <li class="breadcrumb-item">
        <router-link :to="{name: 'explore'}">Explore</router-link>
      </li>
      <li v-if="isInCollection" class="breadcrumb-item">
        <router-link :to="{name: 'collection', params: {address: item.collection.address}}">
          {{ collectionName }}
        </router-link>
      </li>
      <li aria-current="page" class="breadcrumb-item active">
        {{ itemDisplayName }}
      </li>
    </ol>
  </nav>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";

export default defineComponent({
  name: "TheItemBreadcrumb",
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
          index
          name
          collection {
            address
            name
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
        index: "0" as string | null,
        name: null as string | null,
        collection: null as {
          address: string,
          name: string | null,
        } | null,
      }
    }
  },
  computed: {
    isInCollection() {
      return this.item.collection !== null
    },
    collectionName() {
      return this.item.collection?.name ?? "Untitled Collection"
    },
    itemDisplayName: function () {
      return this.item.name ?? ("Item no. " + this.item.index)
    },
  },
})
</script>

<style scoped>

</style>
