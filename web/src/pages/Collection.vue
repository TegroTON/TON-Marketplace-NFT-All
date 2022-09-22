<template>
  <collection-hero :address="address"></collection-hero>
  <main class="main-page">
    <section class="nfr-collection section pt-0">
      <div class="container-fluid">
        <div class="row">
          <div class="col-lg-4 col-xl-4 col-xxl-3 mb-4 mb-lg-0">
            <!-- Start Collection Info -->
            <collection-info-card :address="address"></collection-info-card>
            <!-- End Collection Info -->
            <!-- Start Collection Filters -->
            <collection-filters v-if="false"></collection-filters>
            <!-- End Collection Filters -->
          </div>
          <div class="col-lg-8 col-xl-8 col-xxl-9">
            <!-- Start Collection Stat -->
            <collection-stats-card :address="address"></collection-stats-card>
            <!-- End Collection Stat -->
            <!-- Start Collection Tabs -->
            <div id="mouseScroll" class="overflow-auto mobile__nav-bottom mb-2 px-2">
              <ul id="myTab" class="nav collections__nav list-unstyled d-flex flex-nowrap align-items-center"
                  role="tablist">
                <li class="collections__nav-item">
                  <button id="Owned-tab" aria-controls="Owned"
                          aria-selected="true"
                          class="collections__nav-link d-flex align-items-center text-nowrap active"
                          data-bs-target="#Owned" data-bs-toggle="tab" role="tab"
                          type="button">Items
                  </button>
                </li>
                <li v-if="false" class="collections__nav-item">
                  <button id="Activity-tab" aria-controls="Activity"
                          aria-selected="false" class="collections__nav-link d-flex align-items-center text-nowrap"
                          data-bs-target="#Activity" data-bs-toggle="tab"
                          role="tab" type="button">Activity
                  </button>
                </li>
                <button v-if="false" aria-controls="collapseFilters"
                        aria-expanded="false"
                        class="btn btn-sm btn-secondary ms-auto d-flex align-items-center btn-filter"
                        data-bs-toggle="collapse" href="#collapseFilters"
                        role="button">
                  <i class="fa-regular fa-filter-list me-2"></i> Sort
                </button>
              </ul>
            </div>
            <div v-if="false" id="collapseFilters" class="collections__filters collapse modified-collapse">
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
            <div id="myTabContent" class="tab-content py-4">
              <div id="Owned" aria-labelledby="Owned-tab" class="tab-pane fade show active" role="tabpanel">
                <div class="row">
                  <div v-for="item in collection.items" class="col-sm-6 col-xl-4 col-xxl-3 mb-4">
                    <collection-item-card :address="item.address"></collection-item-card>
                  </div>
                </div>
              </div>
              <div id="Activity" aria-labelledby="Activity-tab" class="tab-pane fade" role="tabpanel">
                <activity-list></activity-list>
              </div>
            </div>
            <!-- End Collection Tabs -->
            <button v-if="false" class="btn btn-outline-secondary mt-3 w-100" type="button">LOAD MORE</button>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import ActivityList from "../components/ActivityList.vue";
import CollectionFilters from "../components/CollectionFilters.vue";
import gql from "graphql-tag";
import CollectionItemCard from "../components/CollectionItemCard.vue";
import CollectionStatsCard from "../components/collection/CollectionStatsCard.vue";
import CollectionInfoCard from "../components/collection/CollectionInfoCard.vue";
import CollectionHero from "../components/collection/CollectionHero.vue";

export default defineComponent({
  name: "Collection",
  components: {
    CollectionHero,
    CollectionInfoCard, CollectionStatsCard, CollectionItemCard, CollectionFilters, ActivityList
  },
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  apollo: {
    collection: {
      query: gql`query collection($address: String!) {
        collection(address: $address) {
          address
          items(take: 25) {
            address
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
      collection: {
        address: this.address,
        items: [] as { address: string }[]
      }
    }
  }
})
</script>

