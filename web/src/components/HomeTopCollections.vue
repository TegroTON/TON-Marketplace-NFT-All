<template>
  <div class="collection__container container-fluid">
    <div class="d-block d-sm-flex align-items-center mb-56">
      <h2 class="section__title mb-0">Top <span class="color-yellow">collections</span></h2>
      <div class="dropdown mt-4 mt-sm-0 ms-0 ms-sm-auto">
        <a id="dropdownMenuLink" aria-expanded="false" class="btn btn-secondary dropdown-toggle"
           data-bs-toggle="dropdown"
           href="#" role="button">
          Last 24 hours
        </a>
        <ul aria-labelledby="dropdownMenuLink" class="dropdown-menu animate slideIn mt-5">
          <li><a class="dropdown-item" href="#">1 Day</a></li>
          <li><a class="dropdown-item" href="#">3 Days</a></li>
          <li><a class="dropdown-item" href="#">7 days</a></li>
        </ul>
      </div>
    </div>
    <div class="row pt-3">
      <div v-for="collection in collections" class="col-sm-6 col-xxl-4">
        <router-link :title="collection.name" :to="collection.link" class="d-block">
          <div class="card d-flex flex-lg-row align-items-center p-3 p-xl-4 rounded-20 mb-4">
            <div class="collection__picbox position-relative me-4 mb-4 mb-lg-0">
              <picture>
                <img :alt="collection.name" :src="collection.image" class="image-80x80 rounded-circle"
                     height="80" loading="lazy" width="80">
              </picture>
              <i class="fa-solid fa-circle-check fs-22 color-yellow position-absolute bottom-0"
                 style="right: -4%;"></i>
            </div>
            <div class="collection__body mb-3 mb-lg-0">
              <h4 class="fs-20 mb-3 text-truncate" style="max-width: 224px">{{ collection.name }}</h4>
              <p class="mb-0 color-grey text-center text-lg-start">
                Floor: <span class="ms-1 text-uppercase">3,02 TON</span>
              </p>
            </div>
            <div class="collection__info d-flex d-lg-block text-center text-lg-end ms-0 ms-lg-auto">
              <div class="fw-medium text-uppercase text-white mb-3 me-3 me-lg-0">3.8k TON</div>
              <div class="fw-medium color-grey">
                $2.68M
                <span class="color-green ms-2">+8.84%</span>
              </div>
            </div>
          </div>
        </router-link>
      </div>
    </div>
    <div class="mt-5 text-center">
      <a class="btn btn-primary" href="#!">Go to ranking</a>
    </div>
  </div>
</template>

<script lang="ts">
import {useTopCollectionsQuery} from "../graphql/generated";
import {defineComponent} from "vue";
import defaultImage from "../assets/user-1.svg";

export default defineComponent({
  name: 'HomeTopCollections',
  setup() {
    const response = useTopCollectionsQuery({variables: {take: 9}})

    return {
      data: response.data
    }
  },
  computed: {
    collections() {
      return (this.data?.collections ?? [])
          .map(value => ({
            link: {name: 'collection', params: {address: value.address}},
            name: value.name ?? 'Untitled Collection',
            image: value.image ?? defaultImage,
          }))
    },
  }
})
</script>
