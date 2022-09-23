<template>
  <entity-info>
    <template #image>
      <img alt="" class="img-fluid rounded-circle" src="/assets/img/author/author-17.jpg">
    </template>
    <template #actions>
      <a class="btn btn-sm btn-outline-primary" href="#!">Subscribe</a>
    </template>
    <template #name>
      <span>TON NFT Tegro Cat</span>
      <i class="fa-solid fa-circle-check fs-22 color-yellow ms-2"></i>
    </template>
    <template #description>
      <p>
        TON Tegro Cat are unique NFTs with cats created only for the TON network. <br>
        Our TON NFT "Cats" is a community of 9,999 super-rare, artfully crafted, collectible cats.
      </p>
      <p class="collection__desc color-grey">
        Each Cat is an individual being. The collection was created by the TegroMoney team, the creators of
        the TGR token on The Open Network blockchain.
      </p>
      <a class="collection__link text-white" href="#!" target="__blank">
        <i class="fa-regular fa-link-simple color-yellow me-2"></i>
        boredapeyachtclub.com
      </a>
    </template>
    <template #footer>
      Created by <span class="color-yellow">Smircs</span>
      <i class="fa-solid fa-circle-check color-yellow fs-14 ms-1"></i>
    </template>
  </entity-info>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import EntityInfo from "./EntityInfo.vue";

export default defineComponent({
  name: "CollectionInfo",
  components: {EntityInfo},
  props: {
    address: {
      type: String,
      required: true,
    }
  }
})
</script>

<style lang="scss" scoped>

</style>
