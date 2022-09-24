<template>
  <collection-layout>
    <template #hero>
      <picture>
        <source srcset="./assets/img/nft-hero.webp" type="image/webp">
        <source srcset="./assets/img/nft-hero.jpg" type="image/jpeg">
        <img alt="Tegro Cat" class="nft-hero__image" height="275" loading="lazy" src="./assets/img/nft-hero.jpg"
             width="340">
      </picture>
    </template>
    <template #left-panel>
      <collection-info :address="address"></collection-info>
      <!-- End Collection Info -->
      <!-- Start Collection Filters -->
      <!--            <?php-->
      <!--                  require "_collection-filters.php";-->
      <!--                  ?>-->
      <!-- End Collection Filters -->
    </template>
    <template #right-panel>
      <!-- Start Collection Stat -->
      <collection-stats :address="address"></collection-stats>
      <!-- End Collection Stat -->
      <!-- Start Collection Tabs -->

      <div id="mouseScroll" class="overflow-auto mobile__nav-bottom mb-2 px-2">
        <div class="d-flex flex-nowrap align-items-center">
          <tabs-bar>
            <tab-bar-button id="tab-button-items" active target="#tab-content-items">Items</tab-bar-button>
            <tab-bar-button id="tab-button-activity" target="#tab-content-activity">Activity</tab-bar-button>
          </tabs-bar>

          <button aria-controls="collapseFilters"
                  aria-expanded="false"
                  class="btn btn-sm btn-secondary ms-auto d-flex align-items-center btn-filter"
                  data-bs-toggle="collapse" href="#collapseFilters"
                  role="button">
            <i class="fa-regular fa-filter-list me-2"></i> Sort
          </button>
        </div>
      </div>
      <div id="collapseFilters" class="collections__filters collapse modified-collapse">
        <div class="d-block d-sm-flex flex-wrap align-items-center">
          <div class="m-3">
            <label class="color-grey mb-2">Category:</label>
            <select aria-label="Select Category" class="form-select border">
              <option selected>All</option>
              <option value="1">One</option>
              <option value="2">Two</option>
              <option value="3">Three</option>
            </select>
          </div>
          <div class="m-3">
            <label class="color-grey mb-2">Properties:</label>
            <select aria-label="Select Category" class="form-select border">
              <option selected>All 354</option>
              <option value="1">One</option>
              <option value="2">Two</option>
              <option value="3">Three</option>
            </select>
          </div>
          <div class="m-3">
            <label class="color-grey mb-2">Sale type:</label>
            <select aria-label="Select Category" class="form-select border">
              <option selected>All</option>
              <option value="1">One</option>
              <option value="2">Two</option>
              <option value="3">Three</option>
            </select>
          </div>
          <div class="m-3">
            <label class="color-grey mb-2">Price range:</label>
            <select aria-label="Select Category" class="form-select border">
              <option selected>ETH 5 - 15</option>
              <option value="1">One</option>
              <option value="2">Two</option>
              <option value="3">Three</option>
            </select>
          </div>
          <div class="m-3 ms-xxl-auto">
            <label class="color-grey mb-2">Sort:</label>
            <select aria-label="Select Category" class="form-select border">
              <option selected>Recently listed</option>
              <option value="1">Price: low to high</option>
              <option value="2">Price: high to low</option>
              <option value="3">Auction ending soon</option>
            </select>
          </div>
        </div>
      </div>
      <tabs-container>
        <tab-content id="tab-content-items" active by="#tab-button-items">
          <collection-items :address="address"></collection-items>
        </tab-content>

        <tab-content id="tab-content-activity" by="#tab-button-activity">
          <button class="btn btn-outline-secondary mt-3 w-100" type="button">LOAD MORE</button>
        </tab-content>
      </tabs-container>
    </template>
  </collection-layout>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import EntityHero from "../components/EntityHero.vue";
import CollectionInfo from "../components/CollectionInfo.vue";
import CollectionStats from "../components/CollectionStats.vue";
import TabBarButton from "../components/TabBarButton.vue";
import CollectionLayout from "../layouts/CollectionLayout.vue";
import TabsBar from "../components/TabsBar.vue";
import TabsContainer from "../components/TabsContainer.vue";
import TabContent from "../components/TabContent.vue";
import CollectionItems from "../components/CollectionItems.vue";

export default defineComponent({
  name: "CollectionView",
  components: {
    CollectionItems,
    TabContent,
    TabsContainer,
    TabsBar, CollectionLayout, TabBarButton, CollectionInfo, CollectionStats, EntityHero
  },
  props: {
    address: {
      required: true,
      type: String,
    }
  },
})
</script>

<style lang="scss" scoped>

</style>
