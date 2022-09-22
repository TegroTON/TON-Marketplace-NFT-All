<template>
  <section class="nft-hero">
    <picture>
      <img :src="collectionHeroImage" alt="Cover Image" class="nft-hero__image" loading="lazy">
    </picture>
  </section>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";

export default defineComponent({
  name: "CollectionHero",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  apollo: {
    collection: {
      query: gql`query collectionHeroImage($address: String!) {
        collection(address: $address) {
          address
          coverImage
          image
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
        address: this.address,
        coverImage: null as string | null,
        image: null as string | null,
      }
    }
  },
  computed: {
    collectionHeroImage() {
      return this.collection.coverImage ?? this.collection.image
    },
  }
})
</script>

<style scoped>

</style>
