<template>
  <section class="nft-hero">
    <picture>
      <source srcset="assets/img/nft-hero.webp" type="image/webp">
      <source srcset="assets/img/nft-hero.jpg" type="image/jpeg">
      <img alt="Tegro Cat" class="nft-hero__image" height="275" loading="lazy" src="assets/img/nft-hero.jpg"
           width="340">
    </picture>
  </section>
  <main class="main-page">
    <section class="nfr-collection section pt-0">
      <div class="container-fluid">
        <div class="row">
          <div class="col-lg-4 col-xl-4 col-xxl-3 mb-4 mb-lg-0">
            <!-- Start Collection Info -->
            <div class="card card-blur p-0 mt--100 mb-4">
              <div class="card__share d-flex position-absolute" style="top: 8px; right: 8px">
                <button class="btn btn-cube" type="button"><i class="fa-regular fa-share-nodes"></i></button>
                <div class="dropdown">
                  <button id="dropdownMenuButton1" aria-expanded="false" class="btn btn-cube" data-bs-toggle="dropdown"
                          type="button">
                    <i class="fa-regular fa-ellipsis-stroke"></i>
                  </button>
                  <ul aria-labelledby="dropdownMenuButton1" class="dropdown-menu">
                    <li><a class="dropdown-item border-0" href="#">Report page</a></li>
                  </ul>
                </div>
              </div>
              <div class="card-body p-4 p-lg-2 p-xl-3 p-xxl-4">
                <div class="d-flex flex-column flex-md-row flex-lg-column align-items-center mb-5">
                  <div class="collection__image mb-4 mb-md-0 mb-lg-4 ms-auto ms-md-0 ms-lg-auto me-auto">
                    <img :src="collection.metadata.image" alt="" class="img-fluid rounded-circle">
                  </div>
                  <div class="d-flex mx-0 mx-lg-auto">
                    <a class="btn btn-sm btn-outline-primary" href="#!">Subscribe</a>
                  </div>
                </div>
                <h1 class="collection__name fs-24 mb-4">
                  <span>{{ collection.metadata.name }}</span>
                  <i class="fa-solid fa-circle-check fs-22 color-yellow ms-2"></i>
                </h1>
                <div class="collection__desc color-grey mb-4">
                  <p>{{ collection.metadata.description }}</p>

                  <a class="collection__link text-white" href="#!" target="__blank">
                    <i class="fa-regular fa-link-simple color-yellow me-2"></i>
                    boredapeyachtclub.com
                  </a>
                </div>
                <div class="libermall__soclinks">
                  <a class="libermall__soclinks-item ms-0" href="#!"><i class="fa-brands fa-telegram"></i></a>
                  <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-discord"></i></a>
                  <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-instagram"></i></a>
                  <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-linkedin-in"></i></a>
                  <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-reddit-alien"></i></a>
                </div>
              </div>
              <div class="card-footer text-center border-top px-5 py-3">
                Created by <span class="color-yellow">{{ collection.contract.owner }}</span>
                <i class="fa-solid fa-circle-check color-yellow fs-14 ms-1"></i>
              </div>
            </div>
            <!-- End Collection Info -->
            <!-- Start Collection Filters -->
            <collection-filters></collection-filters>
            <!-- End Collection Filters -->
          </div>
          <div class="col-lg-8 col-xl-8 col-xxl-9">
            <!-- Start Collection Stat -->
            <div id="mouseScroll" class="card card-blur mb-4 overflow-auto">
              <div class="d-flex align-items-center justify-content-between">
                <div class="card-blur__item p-4 border-end text-center">
                  <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">floor</h5>
                  <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">0.04 eTH</p>
                </div>
                <div class="card-blur__item p-4 border-end text-center">
                  <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">volume</h5>
                  <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">40.61 ETH</p>
                </div>
                <div class="card-blur__item p-4 border-end text-center">
                  <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">Items</h5>
                  <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">{{
                      collection.contract.size
                    }}</p>
                </div>
                <div class="card-blur__item p-4 border-end text-center">
                  <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">Owners</h5>
                  <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">461</p>
                </div>
                <div class="card-blur__item p-4 border-end text-center">
                  <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">Blockchain</h5>
                  <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">TON Coin</p>
                </div>
                <div class="card-blur__item p-4 text-center">
                  <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">address</h5>
                  <p class="m-0 text-uppercase fw-medium text-truncate" style="letter-spacing: 1px;">{{ address }}</p>
                </div>
              </div>
            </div>
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
                <li class="collections__nav-item">
                  <button id="Activity-tab" aria-controls="Activity"
                          aria-selected="false" class="collections__nav-link d-flex align-items-center text-nowrap"
                          data-bs-target="#Activity" data-bs-toggle="tab"
                          role="tab" type="button">Activity
                  </button>
                </li>
                <button aria-controls="collapseFilters"
                        aria-expanded="false"
                        class="btn btn-sm btn-secondary ms-auto d-flex align-items-center btn-filter"
                        data-bs-toggle="collapse" href="#collapseFilters"
                        role="button">
                  <i class="fa-regular fa-filter-list me-2"></i> Sort
                </button>
              </ul>
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
            <div id="myTabContent" class="tab-content py-4">
              <div id="Owned" aria-labelledby="Owned-tab" class="tab-pane fade show active" role="tabpanel">
                <collection-list></collection-list>
              </div>
              <div id="Activity" aria-labelledby="Activity-tab" class="tab-pane fade" role="tabpanel">
                <activity-list></activity-list>
              </div>
            </div>
            <!-- End Collection Tabs -->
            <button class="btn btn-outline-secondary mt-3 w-100" type="button">LOAD MORE</button>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import ActivityList from "../components/ActivityList.vue";
import CollectionList from "../components/CollectionList.vue";
import CollectionFilters from "../components/CollectionFilters.vue";
import gql from "graphql-tag";

export default defineComponent({
  name: "Collection",
  components: {CollectionFilters, CollectionList, ActivityList},
  props: {
    address: String,
  },
  apollo: {
    collection: {
      query: gql`query collection($address: String!) {
        collection(address: $address) {
          contract {
            owner
            size
          }
          metadata {
            name
            description
            image
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
        contract: {
          owner: "",
          size: 0,
        },
        metadata: {
          name: "Loading...",
          description: "",
          image: "",
        }
      }
    }
  }
})
</script>

