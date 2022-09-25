<template>
  <footer class="pt-24 px-4 md:px-12 pb-12">
    <div class="container mx-auto px-3">
      <div class="grid grid-cols-1 lg:grid-cols-3">
        <div class="cols-span-2 mb-12 lg:mb-0">
          <div class="mb-6 items-center">
            <img alt="Libermall - NFT Marketplace" src="../assets/logo/apple-icon-57x57.png">
            <span class="mt-4 text-2xl hidden md:block">Libermall</span>
          </div>
          <div class="mb-12">
            <h4 class="text-lg font-raleway mb-2">Stay in the loop</h4>
            <p class="text-gray-500">
              Join our mailing list to stay in the loop with our newest feature releases, NFT drops, and tips and tricks
              for navigating OpenSea.
            </p>
          </div>
          <div class="flex">
            <a class="bg-gray-900 flex w-12 h-12 mx-1.5 items-center justify-center rounded-lg" href="#!">
              <i class="fa-brands fa-telegram"></i>
            </a>
            <a class="bg-gray-900 flex w-12 h-12 mx-1.5 items-center justify-center rounded-lg" href="#!">
              <i class="fa-brands fa-discord"></i>
            </a>
            <a class="bg-gray-900 flex w-12 h-12 mx-1.5 items-center justify-center rounded-lg" href="#!">
              <i class="fa-brands fa-twitter"></i>
            </a>
            <a class="bg-gray-900 flex w-12 h-12 mx-1.5 items-center justify-center rounded-lg" href="#!">
              <i class="fa-brands fa-instagram"></i>
            </a>
          </div>
        </div>
        <div class="py-4 lg:py-0 border-b border-b-gray-900">
          <h4 class="tracking-tight uppercase flex items-center">
            <span class="mr-auto">Marketplace</span>
            <i class="fa-regular fa-angle-down block lg:hidden"></i>
          </h4>
          <ul class="mt-6 lg:mt-0">
            <li class="mb-2">
              <a class="text-gray-500 hover:text-yellow" href="#!">All NFTs</a>
            </li>
          </ul>
        </div>
      </div>
      <div class="pt-12 mt-12 text-gray-500 border-t border-t-gray-900 grid grid-cols-1 md:grid-cols-2">
        <div class="mb-4 md:mb-0">
          <span class="me-auto">©2022 Libermall, Inc</span>
        </div>
        <div class="mb-4 md:mb-0 ml-auto text-end">
          <a class="mr-4 hover:text-yellow" href="#!">Privacy Policy</a>
          <a class="hover:text-yellow" href="#!">Terms of Service</a>
        </div>
      </div>
    </div>
  </footer>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "AppFooter",
})
</script>
