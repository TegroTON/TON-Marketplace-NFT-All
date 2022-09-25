<template>
  <header class="sticky z-40 top-0 py-6 sm:py-7  bg-opacity-80 bg-dark-900 backdrop-blur-2xl">
    <div class="container relative mx-auto px-2.5">
      <nav class="relative flex flex-wrap p-0 items-center justify-between">
        <router-link class="flex items-center mr-12 no-underline text-gray-500" to="/">
          <img alt="Libermall - NFT Marketplace" class="w-12 h-12 object-contain"
               src="../assets/logo/apple-icon-57x57.png">
          <span class="font-raleway text-3xl font-bold ml-6 text-white hidden 2xl:block">Libermall</span>
        </router-link>
        <base-secondary-button class="lg:hidden  text-xl" @click="toggleNavbar">
          <i v-if="isNavbarOpen" class="fa-regular fa-xmark"></i>
          <i v-else class="fa-regular fa-bars"></i>
        </base-secondary-button>
        <div
            :class="isNavbarOpen ? 'fixed' : 'hidden'"
            class="left-0 top-0 px-3 py-6 w-3/4 h-screen overflow-auto bg-dark-900 basis-full flex-grow items-center  lg:h-auto lg:flex lg:basis-auto">
          <form class="mb-4 xl:mb-0 mx-0 xl:mx-12 order-1 lg:order-2 flex-auto lg:hidden xl:block" disabled>
            <div
                class="border border-solid border-border-soft bg-soft rounded-lg flex flex-wrap items-stretch w-full">
              <input
                  class="rounded-lg rounded-tr-none rounded-br-none bg-transparent border-0 border-gray-900 focus:ring-gray-700 px-3 py-1.5 flex-auto min-w-0 text-white"
                  placeholder="Search ..." type="text">
              <div
                  class="-ml-1 rounded-tl-none rounded-bl-none rounded-md p-0 flex items-center text-center">
                <base-secondary-button type="submit">
                  <i class="fa-solid fa-magnifying-glass text-gray-500"></i>
                </base-secondary-button>
              </div>
            </div>
          </form>
          <div class="relative order-4 lg:order-1">
            <router-link to="/explore'">
              <base-primary-button
                  class="flex items-center flex-nowrap text-sm uppercase font-medium border"
                  type="button">
                <i class="fa-regular fa-grid-2 mr-2"></i> Explore
              </base-primary-button>
            </router-link>
          </div>
          <ul class="pl-0 mb-0 list-none pr-12 py-6 ml-0 lg:ml-auto order-2 lg:order-3 flex flex-row text-gray-500">
            <li class="uppercase px-5 py-0 font-medium text-sm">
              <a href="#">Stats</a>
            </li>
            <li class="uppercase px-5 py-0 font-medium text-sm">
              <a href="#">Resources</a>
            </li>
          </ul>
          <!-- TODO -->
          <div v-if="isConnected" class="dropdown dropstart order-3 order-lg-4">
            <button id="dropdownMenuProfile" aria-expanded="false" data-bs-toggle="dropdown" type="button">
              <img alt="" class="rounded-circle profile-image" height="42" src="../assets/user-1.svg" width="42">
            </button>
            <ul aria-labelledby="dropdownMenuProfile" class="dropdown-menu">
              <li>
                <a class="dropdown-item" href="/my-profile.php"><i class="fa-regular fa-user me-3"></i>Profile</a>
              </li>
              <li>
                <a class="dropdown-item border-0" @click="disconnect()">
                  <i class="fa-regular fa-link-simple-slash me-3"></i>Disconnect
                </a>
              </li>
            </ul>
          </div>
          <connect></connect>
        </div>
      </nav>
    </div>
  </header>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import Connect from "./Connect.vue";
import {useConnectionStore} from "../stores/ConnectionStore";
import {mapActions, mapState} from "pinia";
import BaseSecondaryButton from "./base/BaseSecondaryButton.vue";
import BasePrimaryButton from "./base/BasePrimaryButton.vue";

export default defineComponent({
  name: "AppHeader",
  components: {BasePrimaryButton, BaseSecondaryButton, Connect},
  data() {
    return {
      isNavbarOpen: false
    }
  },
  computed: {
    ...mapState(useConnectionStore, ['isConnected', 'walletAddress']),
  },
  methods: {
    ...mapActions(useConnectionStore, ['disconnect']),
    toggleNavbar() {
      this.isNavbarOpen = !this.isNavbarOpen
    },
  }
})
</script>
