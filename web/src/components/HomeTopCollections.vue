<template>
  <div class="container px-4">
    <div class="block sm:flex items-center mb-14">
      <h2 class="font-raleway text-5xl font-medium mb-0">
        Top <span class="text-yellow">collections</span>
      </h2>
    </div>
    <div class="grid grid-cols-2 xl:grid-cols-3">
      <div
        v-for="collection in collections"
        :key="collection.index"
        class="col"
      >
        <router-link
          :title="collection.name"
          :to="collection.link"
          class="block"
        >
          <div
            class="flex items-center flex-col p-4 xl:p-6 rounded-3xl mb-6 bg-dark-700"
          >
            <div class="relative top-0 left-0 mb-4 mr-6">
              {{ collection.index }}
            </div>
            <div class="relative h-16 mb-6 mr-6">
              <picture>
                <img
                  :alt="collection.name"
                  :src="collection.image"
                  class="object-cover w-full h-full rounded-full"
                  height="80"
                  loading="lazy"
                  width="80"
                />
              </picture>
              <i
                class="fa-solid fa-circle-check text-xl text-yellow absolute bottom-0 -right-0.5"
              ></i>
            </div>
            <div class="mb-4 lg:mb-0">
              <h4 class="font-raleway text-lg mb-4 truncate">
                {{ collection.name }}
              </h4>
              <p class="mb-0 text-gray-500 text-center lg:text-start">
                Floor: <span class="ml-1 uppercase">3,02 TON</span>
              </p>
            </div>
            <div class="flex lg:block text-center lg:text-end ml-0 lg:ml-auto">
              <div class="font-medium uppercase text-white mb-4 mr-4 lg:mr-0">
                3.8k TON
              </div>
              <div class="font-medium text-gray-500">
                $2.68M
                <span class="text-green ml-2">+8.84%</span>
              </div>
            </div>
          </div>
        </router-link>
      </div>
    </div>
    <div class="mt-12 text-center">
      <base-button primary>Go to ranking</base-button>
    </div>
  </div>
</template>

<script lang="ts">
import { useTopCollectionsQuery } from "../graphql/generated";
import { defineComponent } from "vue";
import defaultImage from "../assets/user-1.svg";
import BaseButton from "./base/BaseButton.vue";

export default defineComponent({
  name: "HomeTopCollections",
  components: { BaseButton },
  setup() {
    const response = useTopCollectionsQuery({ variables: { take: 9 } });

    return {
      data: response.data,
    };
  },
  computed: {
    collections() {
      return (this.data?.collections ?? []).map((value, index) => ({
        index: index + 1,
        link: { name: "collection", params: { address: value.address } },
        name: value.name ?? "Untitled Collection",
        image: value.image ?? defaultImage,
      }));
    },
  },
});
</script>
