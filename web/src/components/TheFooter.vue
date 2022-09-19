<template>
  <footer class="footer">
    <div class="container-fluid">
      <div id="accordionFooter" class="row accordion modified-collapse">
        <div class="col-md-12 col-lg-3 me-auto mb-5 mb-lg-0">
          <div class="footer__logo d-flex align-items-center mb-4">
            <img alt="Libermall - NFT Marketplace" class="navbar-logo__img"
                 src="assets/img/logo/apple-icon-57x57.png">
            <span class="navbar-logo__name fs-24 d-none d-md-block ms-3">Libermall</span>
          </div>
          <div class="footer__text mb-5">
            <h4 class="fs-18">Stay in the loop</h4>
            <p class="color-grey">
              Follow our social media channels to stay in the loop with our newest feature releases, NFT drops, and tips
              and tricks for navigating Libermall.
            </p>
            <p v-if="false" class="color-grey">
              Join our mailing list to stay in the loop with our newest feature releases, NFT drops, and tips and tricks
              for navigating OpenSea.
            </p>
          </div>
          <div v-if="false" class="input-group mb-5">
               <span id="basic-addon1" class="input-group-text">
               <i class="fa-regular fa-envelope fs-18 color-grey"></i>
               </span>
            <input class="form-control" placeholder="Enter email address" type="text">
            <div class="input-group-text p-1">
              <button class="btn btn-primary" style="min-width: 100px;">Send</button>
            </div>
          </div>
          <div class="libermall__soclinks">
            <a class="libermall__soclinks-item" href="https://t.me/LiberMall"><i class="fa-brands fa-telegram"></i></a>
            <a class="libermall__soclinks-item" href="https://twitter.com/LiberMallNFT"><i
                class="fa-brands fa-twitter"></i></a>
          </div>
        </div>
        <div class="col-md-12 col-lg-2 py-3 py-lg-0">
          <h4 aria-controls="FooterMenu-one" aria-expanded="false"
              class="d-flex align-items-center text-uppercase fs-18 mb-0 mb-lg-4" data-bs-target="#FooterMenu-one"
              data-bs-toggle="collapse"
              style="letter-spacing: 1px;">
            <span class="me-auto">Marketplace</span>
            <i class="fa-regular fa-angle-down d-block d-lg-none"></i>
          </h4>
          <ul id="FooterMenu-one" class="list-unstyled accordion-collapse collapse mt-4 mt-lg-0"
              data-bs-parent="#accordionFooter">
            <li class="mb-2">
              <router-link :to="{name: 'explore'}">All NFTs</router-link>
            </li>
            <li v-if="false" class="mb-2">
              <a href="#!">Solana NFTs</a>
            </li>
            <li v-if="false" class="mb-2">
              <a href="#!">Art</a>
            </li>
            <li v-if="false" class="mb-2">
              <a href="#!">Collectibles</a>
            </li>
            <li v-if="false" class="mb-2">
              <a href="#!">Domain Names</a>
            </li>
            <li v-if="false" class="mb-2">
              <a href="#!">Music</a>
            </li>
            <li v-if="false" class="mb-2">
              <a href="#!">Photography</a>
            </li>
            <li v-if="false" class="mb-2">
              <a href="#!">Sports</a>
            </li>
            <li v-if="false" class="mb-2">
              <a href="#!">Trading Cards</a>
            </li>
            <li v-if="false" class="mb-2">
              <a href="#!">Utility</a>
            </li>
            <li v-if="false">
              <a href="#!">Virtual Worlds</a>
            </li>
          </ul>
        </div>
        <div v-if="false" class="col-md-12 col-lg-2 py-3 py-lg-0">
          <h4 aria-controls="FooterMenu-two" aria-expanded="false"
              class="d-flex align-items-center text-uppercase fs-18 mb-0 mb-lg-4" data-bs-target="#FooterMenu-two"
              data-bs-toggle="collapse"
              style="letter-spacing: 1px;">
            <span class="me-auto">My Account</span>
            <i class="fa-regular fa-angle-down d-block d-lg-none"></i>
          </h4>
          <ul id="FooterMenu-two" class="list-unstyled accordion-collapse collapse mt-4 mt-lg-0"
              data-bs-parent="#accordionFooter">
            <li class="mb-2">
              <a href="#!">Profile</a>
            </li>
            <li class="mb-2">
              <a href="#!">Favorites</a>
            </li>
            <li class="mb-2">
              <a href="#!">Watchlist</a>
            </li>
            <li class="mb-2">
              <a href="#!">My Collections</a>
            </li>
            <li class="mb-2">
              <a href="#!">Settings</a>
            </li>
          </ul>
        </div>
        <div v-if="false" class="col-md-12 col-lg-2 py-3 py-lg-0">
          <h4 aria-controls="FooterMenu-three" aria-expanded="false"
              class="d-flex align-items-center text-uppercase fs-18 mb-0 mb-lg-4" data-bs-target="#FooterMenu-three"
              data-bs-toggle="collapse"
              style="letter-spacing: 1px;">
            <span class="me-auto">Resources</span>
            <i class="fa-regular fa-angle-down d-block d-lg-none"></i>
          </h4>
          <ul id="FooterMenu-three" class="list-unstyled accordion-collapse collapse mt-4 mt-lg-0"
              data-bs-parent="#accordionFooter">
            <li class="mb-2">
              <a href="#!">Help Center</a>
            </li>
            <li class="mb-2">
              <a href="#!">Platform Status</a>
            </li>
            <li class="mb-2">
              <a href="#!">Partners</a>
            </li>
            <li class="mb-2">
              <a href="#!">Gas-Free Marketplace</a>
            </li>
            <li class="mb-2">
              <a href="#!">Taxes</a>
            </li>
            <li class="mb-2">
              <a href="#!">Blog</a>
            </li>
            <li class="mb-2">
              <a href="#!">Docs</a>
            </li>
            <li>
              <a href="#!">Newsletter</a>
            </li>
          </ul>
        </div>
        <div v-if="false" class="col-md-12 col-lg-2 py-3 py-lg-0">
          <h4 aria-controls="FooterMenu-four" aria-expanded="false"
              class="d-flex align-items-center text-uppercase fs-18 mb-0 mb-lg-4" data-bs-target="#FooterMenu-four"
              data-bs-toggle="collapse"
              style="letter-spacing: 1px;">
            <span class="me-auto">Company</span>
            <i class="fa-regular fa-angle-down d-block d-lg-none"></i>
          </h4>
          <ul id="FooterMenu-four" class="list-unstyled accordion-collapse collapse mt-4 mt-lg-0"
              data-bs-parent="#accordionFooter">
            <li class="mb-2">
              <a href="#!">About</a>
            </li>
            <li class="mb-2">
              <a href="#!">Careers</a>
            </li>
            <li class="mb-2">
              <a href="#!">Ventures</a>
            </li>
            <li>
              <a href="#!">Grants</a>
            </li>
          </ul>
        </div>
      </div>
      <div class="footer__bottom color-grey border-top pt-5 mt-5">
        <div class="row">
          <div class="col-sm-12 col-md-6 mb-3 mb-md-0">
            <span class="me-auto">©2022 Libermall, Inc</span>
          </div>
          <div v-if="false" class="col-sm-12 col-md-6 ms-auto text-end">
            <a class="me-3" href="#!">Privacy Police</a>
            <a href="#!">Terms of Service</a>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "TheFooter"
})
</script>
