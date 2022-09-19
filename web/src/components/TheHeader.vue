<template>
  <header class="header">
    <div class="container-fluid">
      <nav class="navbar navbar-expand-lg d-flex align-items-center p-0">
        <a class="navbar-logo me-5" href="/">
          <img alt="Libermall - NFT Marketplace" class="navbar-logo__img" src="assets/img/logo/apple-icon-57x57.png">
          <span class="navbar-logo__name d-none d-xxl-block">Libermall</span>
        </a>
        <button aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation"
                class="navbar-toggler btn btn-secondary order-2 order-xl-1 ms-3" data-bs-target="#navbarHeader"
                data-bs-toggle="collapse"
                type="button">
          <i class="fa-regular fa-bars fs-20"></i>
        </button>
        <div id="navbarHeader" class="collapse navbar-collapse ">
          <form v-if="false"
                class="header__search flex-fill d-block d-lg-none d-xl-block mx-0 mx-lg-5 mb-3 mb-lg-0 order-1 order-lg-2">
            <div class="input-group">
              <input aria-describedby="basic-addon2" aria-label="Search" class="form-control" placeholder="Search ..."
                     type="text">
              <div class="input-group-text p-0">
                <button class="btn" type="submit">
                  <i class="fa-solid fa-magnifying-glass color-grey"></i>
                </button>
              </div>
            </div>
          </form>
          <div class="dropdown order-4 order-lg-1">
            <router-link :to="{name: 'explore'}" class="btn btn-primary d-flex flex-nowrap align-items-center">
              <i class="fa-regular fa-grid-2 me-2"></i>
              Explore
            </router-link>
            <button v-if="false" id="dropdownMenuCat" aria-expanded="false"
                    class="btn btn-primary d-flex flex-nowrap align-items-center"
                    data-bs-toggle="dropdown" type="button">
              <i class="fa-regular fa-grid-2 me-2"></i>
              Explore
            </button>
            <ul v-if="false" aria-labelledby="dropdownMenuCat" class="dropdown-menu animate slideIn mt-3">
              <li><a class="dropdown-item" href="#"><i class="fa-regular fa-hexagon-vertical-nft-slanted me-3"></i>All
                NFTs</a></li>
              <li><a class="dropdown-item" href="#"><i class="fa-regular fa-hexagon-image me-3"></i>Solanas</a></li>
              <li><a class="dropdown-item" href="#"><i class="fa-regular fa-paintbrush-fine me-3"></i>Art</a></li>
              <li><a class="dropdown-item" href="#"><i class="fa-regular fa-rabbit me-3"></i>Collectibles</a></li>
              <li><a class="dropdown-item" href="#"><i class="fa-regular fa-server me-3"></i>Domain Names</a></li>
              <li><a class="dropdown-item" href="#"><i class="fa-regular fa-music me-3"></i>Music</a></li>
              <li><a class="dropdown-item" href="#"><i class="fa-regular fa-camera me-3"></i>Photography</a></li>
              <li><a class="dropdown-item" href="#"><i class="fa-regular fa-futbol me-3"></i>Sports</a></li>
              <li><a class="dropdown-item" href="#"><i class="fa-regular fa-heart-circle-bolt me-3"></i>Trading
                Cards</a></li>
              <li><a class="dropdown-item" href="#"><i class="fa-regular fa-money-bills-simple me-3"></i>Utility</a>
              </li>
              <li><a class="dropdown-item" href="#"><i class="fa-regular fa-vr-cardboard me-3"></i>Virtual Worlds</a>
              </li>
            </ul>
          </div>
          <ul v-if="false" class="navbar-nav d-flex flex-row py-4 py-lg-0 ms-0 ms-lg-auto pe-5 order-2 order-lg-3">
            <li class="nav-item">
              <a class="nav-link" href="#">Stats</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Resources</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Create</a>
            </li>
          </ul>
          <!-- TODO: Show this when signed in -->
          <div v-if="false" class="dropdown dropstart order-3 order-lg-4">
            <button id="dropdownMenuProfile" aria-expanded="false" data-bs-toggle="dropdown" type="button">
              <img alt="" class="rounded-circle profile-image" height="42" src="assets/img/user-1.svg" width="42">
            </button>
            <ul aria-labelledby="dropdownMenuProfile" class="dropdown-menu">
              <li><a class="dropdown-item" href="/my-profile.php"><i class="fa-regular fa-user me-3"></i>Profile</a>
              </li>
              <li><a class="dropdown-item" href="/create-nft.php"><i
                  class="fa-regular fa-hexagon-vertical-nft me-3"></i>Create NFT</a></li>
              <li><a class="dropdown-item border-0" href="#"><i class="fa-regular fa-link-simple-slash me-3"></i>Disconnect</a>
              </li>
            </ul>
          </div>
          <!--          TODO: Fix that-->
          <button v-if="false"
                  class="btn btn-soft d-flex flex-nowrap align-items-center btn-mobile-fixed order-3 order-lg-4"
                  data-bs-target="#ConnectModal"
                  data-bs-toggle="modal" type="button">
            <i class="fa-regular fa-arrow-right-to-arc me-2"></i>
            Connect
          </button>
        </div>
      </nav>
    </div>
  </header>
  <connect-modal></connect-modal>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import ConnectModal from "../modals/ConnectModal.vue";

export default defineComponent({
  name: 'TheHeader',
  components: {ConnectModal}
})
</script>
