<template>
  <main class="main-page">
    <section class="collections section">
      <div class="container-fluid">
        <h1 class="section__title">
          Explore collections
        </h1>
        <div id="mouseScroll" class="overflow-auto mobile__nav-bottom mb-5">
          <ul class="collections__nav list-unstyled d-flex flex-nowrap">
            <li class="collections__nav-item">
              <a class="collections__nav-link active text-nowrap" href="#!">All</a>
            </li>
            <li v-if="false" class="collections__nav-item">
              <a class="collections__nav-link text-nowrap" href="#!">Top</a>
            </li>
            <li v-if="false" class="collections__nav-item">
              <a class="collections__nav-link text-nowrap" href="#!">Art</a>
            </li>
            <li v-if="false" class="collections__nav-item">
              <a class="collections__nav-link text-nowrap" href="#!">Collectibles</a>
            </li>
            <li v-if="false" class="collections__nav-item">
              <a class="collections__nav-link text-nowrap" href="#!">Domain Names</a>
            </li>
            <li v-if="false" class="collections__nav-item">
              <a class="collections__nav-link text-nowrap" href="#!">Music</a>
            </li>
            <li v-if="false" class="collections__nav-item">
              <a class="collections__nav-link text-nowrap" href="#!">Photography</a>
            </li>
            <li v-if="false" class="collections__nav-item">
              <a class="collections__nav-link text-nowrap" href="#!">Sports</a>
            </li>
            <li v-if="false" class="collections__nav-item">
              <a class="collections__nav-link text-nowrap" href="#!">Trading Cards</a>
            </li>
            <li v-if="false" class="collections__nav-item">
              <a class="collections__nav-link text-nowrap" href="#!">Utility</a>
            </li>
            <li v-if="false" class="collections__nav-item">
              <a class="collections__nav-link text-nowrap" href="#!">Virtual Worlds</a>
            </li>
          </ul>
        </div>
        <div class="row collections__list">
          <!--Card Item-->
          <div v-for="collection in collections" class="col-sm-6 col-lg-4 col-xl-3 mb-4">
            <collection-card :address="collection.address"></collection-card>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";
import CollectionCard from "../components/CollectionCard.vue";

export default defineComponent({
  name: "Explore",
  components: {CollectionCard},
  apollo: {
    collections: gql`query {
      collections {
        address
      }
    }`
  },
  data() {
    return {
      collections: [] as { address: String }[]
    }
  }
})
</script>
