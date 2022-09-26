<template>
  <main class="lg:mx-6 mx-3">
    <!-- Start of Hero section
       ============================================= -->
    <section class="relative px-4 py-16 bg-dark-900">
      <agile
        :initial-slide="0"
        :nav-buttons="false"
        :slides-to-show="1"
        class="border border-gray-900 rounded-2xl"
      >
        <div class="container px-3 relative text-center md:text-left">
          <div class="pt-60 px-6 pb-12 w-full">
            <h1 class="text-4xl md:text-6xl font-raleway font-bold mb-7">
              Discover the Exclusive <span class="block">NFT Collection</span>
            </h1>
            <div class="text-lg md:text-2xl text-gray-500 mb-10">
              Click My Collections and set up your collection. <br />
              Add social links, a description, profile & banner images, <br />
              and set a secondary sales fee.
            </div>
            <base-button primary>
              <span>Learn more</span>
            </base-button>
          </div>
          <picture class="">
            <source srcset="../assets/hero-image-1.webp" type="image/webp" />
            <source srcset="../assets/hero-image-1.jpg" type="image/jpeg" />
            <img
              alt="Libermall NFT Marketplace"
              class="absolute top-0 right-0 -z-10 w-full h-full object-cover max-w-none opacity-60"
              src="../assets/hero-image-1.jpg"
            />
          </picture>
        </div>
        <div class="container px-3 relative text-center md:text-left">
          <div class="pt-60 px-6 pb-12 w-full">
            <h1 class="text-4xl md:text-6xl font-raleway font-bold mb-7">
              Discover the Exclusive <span class="block">NFT Collection</span>
            </h1>
            <div class="text-lg md:text-2xl text-gray-500 mb-10">
              Click My Collections and set up your collection. <br />
              Add social links, a description, profile & banner images, <br />
              and set a secondary sales fee.
            </div>
            <base-button primary>
              <span>Learn more</span>
            </base-button>
          </div>
          <picture class="">
            <source srcset="../assets/hero-image-2.webp" type="image/webp" />
            <source srcset="../assets/hero-image-2.jpg" type="image/jpeg" />
            <img
              alt="Libermall NFT Marketplace"
              class="absolute top-0 right-0 -z-10 w-full h-full object-cover max-w-none opacity-60"
              src="../assets/hero-image-2.jpg"
            />
          </picture>
        </div>
      </agile>
    </section>
    <!-- End of Hero section
       ============================================= -->
    <!-- Start of Discover section
       ============================================= -->
    <section class="relative pb-16 px-4 pt-32">
      <div class="container px-3 relative text-center">
        <h2 class="mb-4 font-raleway text-5xl font-bold">
          Discover, Collect and sell
          <span class="text-yellow">extraordinary NFTs</span>
        </h2>
        <p class="text-2xl text-gray-500 font-light mb-12">
          Libermall is the world's first and largest NFT marketplace
        </p>
        <div class="flex flex-wrap items-center justify-center">
          <router-link class="m-4" to="/explore">
            <base-button primary>Explore</base-button>
          </router-link>
          <router-link class="m-4" to="/">
            <base-button secondary>+ Create</base-button>
          </router-link>
        </div>
      </div>
    </section>
    <!-- End of Discover section
       ============================================= -->
    <!-- Start of Libermall section
       ============================================= -->
    <section class="py-16 px-4 relative">
      <div
        class="container px-10 py-12 bg-gradient-to-r from-yellow-gradient-start to-yellow-gradient-end rounded-xl"
      >
        <h2 class="mb-4 font-raleway text-dark-900 text-3xl font-normal">
          TON is in beta <span class="block md:inline">on Libermall</span>
        </h2>
        <div class="flex mt-10 align-items-center">
          <router-link to="/explore">
            <base-button primary> Explore </base-button>
          </router-link>
          <div class="ml-12 hidden md:flex text-dark-900">
            <a
              class="flex w-12 h-12 mx-1.5 items-center justify-center"
              href="#!"
            >
              <i class="fa-brands fa-telegram"></i>
            </a>
            <a
              class="flex w-12 h-12 mx-1.5 items-center justify-center"
              href="#!"
            >
              <i class="fa-brands fa-discord"></i>
            </a>
            <a
              class="flex w-12 h-12 mx-1.5 items-center justify-center"
              href="#!"
            >
              <i class="fa-brands fa-twitter"></i>
            </a>
            <a
              class="flex w-12 h-12 mx-1.5 items-center justify-center"
              href="#!"
            >
              <i class="fa-brands fa-instagram"></i>
            </a>
          </div>
        </div>
      </div>
    </section>
    <!-- End of Libermall section
       ============================================= -->
    <!-- Start of Notable section
       ============================================= -->
    <section v-if="false" id="notable" class="notable section">
      <div class="notable__container container-fluid">
        <div class="notable__head">
          <h2 class="section__title mb-5">
            Notable <span class="color-yellow">drops</span>
          </h2>
        </div>
        <div class="notable-slider pt-3">
          <a
            class="card card-gradient top-effect"
            href="/collection.php"
            title="TODO: Name"
          >
            <div class="card__bage color-yellow text-uppercase">
              <i class="fa-solid fa-fire-flame-curved me-1"></i>
              Live
            </div>
            <picture>
              <source srcset="./assets/img/nft/nft-1.webp" type="image/webp" />
              <source srcset="./assets/img/nft/nft-1.jpg" type="image/jpeg" />
              <img
                alt="TODO: Name"
                class="rounded card-img"
                height="390"
                loading="lazy"
                src="./assets/img/nft/nft-1.jpg"
                width="300"
              />
            </picture>
            <div class="card-body">
              <h3 class="fs-20 my-3">TODO: Name</h3>
              <p class="d-flex align-items-center mb-0 color-grey">
                <span class="icon-ton me-2"></span>
                floor price: 0.5
              </p>
            </div>
          </a>
        </div>
      </div>
    </section>
    <!-- End of Notable section
       ============================================= -->
    <!-- Start of Collections section
       ============================================= -->
    <section class="py-16 relative">
      <home-top-collections></home-top-collections>
    </section>
    <!-- End of Collections section
       ============================================= -->
    <!-- Start of Create section
       ============================================= -->
    <section id="create" class="create section">
      <div class="create__container container-fluid position-relative">
        <h2 class="section__title">
          Create and sell <span class="color-yellow">your NFTs</span>
        </h2>
        <div id="overflow-auto" class="d-flex flex-nowrap overflow-auto py-5">
          <div class="create-card d-flex">
            <div class="create-card__icon d-none d-md-block">
              <img
                alt=""
                class="create-card__img"
                height="100px"
                src="../assets/create-card-1.png"
                width="100px"
              />
            </div>
            <div class="create-card__body ms-4">
              <div class="create-card__num color-grey mb-3">
                0<span class="color-yellow">1</span>
              </div>
              <h4 class="fs-24 mb-4">Set up your wallet</h4>
              <p class="fs-18 color-grey mb-0">
                Once you’ve set up your wallet of choice, connect it to
                Libermall by clicking the wallet icon in the top right corner.
                Learn about the wallets we support
              </p>
            </div>
          </div>
          <div class="create-card d-flex">
            <div class="create-card__icon d-none d-md-block">
              <img
                alt=""
                class="create-card__img"
                height="100px"
                src="../assets/create-card-2.png"
                width="100px"
              />
            </div>
            <div class="create-card__body ms-4">
              <div class="create-card__num color-grey mb-3">
                0<span class="color-yellow">2</span>
              </div>
              <h4 class="fs-24 mb-4">Create your collection</h4>
              <p class="fs-18 color-grey mb-0">
                Once you’ve set up your wallet of choice, connect it to
                Libermall by clicking the wallet icon in the top right corner.
                Learn about the wallets we support
              </p>
            </div>
          </div>
          <div class="create-card d-flex">
            <div class="create-card__icon d-none d-md-block">
              <img
                alt=""
                class="create-card__img"
                height="90px"
                src="../assets/create-card-3.png"
                width="90px"
              />
            </div>
            <div class="create-card__body ms-4">
              <div class="create-card__num color-grey mb-3">
                0<span class="color-yellow">3</span>
              </div>
              <h4 class="fs-24 mb-4">Add your NFTs</h4>
              <p class="fs-18 color-grey mb-0">
                Once you’ve set up your wallet of choice, connect it to
                Libermall by clicking the wallet icon in the top right corner.
                Learn about the wallets we support
              </p>
            </div>
          </div>
          <div class="create-card d-flex">
            <div class="create-card__icon d-none d-md-block">
              <img
                alt=""
                class="create-card__img"
                height="90px"
                src="../assets/create-card-4.png"
                width="90px"
              />
            </div>
            <div class="create-card__body ms-4">
              <div class="create-card__num color-grey mb-3">
                0<span class="color-yellow">4</span>
              </div>
              <h4 class="fs-24 mb-4">List them for sale</h4>
              <p class="fs-18 color-grey mb-0">
                Once you’ve set up your wallet of choice, connect it to
                Libermall by clicking the wallet icon in the top right corner.
                Learn about the wallets we support
              </p>
            </div>
          </div>
        </div>
        <div class="create-border"></div>
      </div>
    </section>
    <!-- End of Collections section
       ============================================= -->
    <!-- Start of Category section
       ============================================= -->
    <section v-if="false" id="category" class="category section pt-150">
      <div class="category__container container-fluid">
        <div class="category__head">
          <h2 class="section__title">
            Browse <span class="color-yellow">by category</span>
          </h2>
        </div>
        <div class="category-slider pt-3">
          <a class="card top-effect" href="/single-collection.php">
            <picture>
              <source
                srcset="./assets/img/category/cat-1.webp"
                type="image/webp"
              />
              <source
                srcset="./assets/img/category/cat-1.jpg"
                type="image/jpeg"
              />
              <img
                alt="Ragnarok"
                class="rounded image-300x300"
                height="275"
                loading="lazy"
                src="./assets/img/category/cat-1.jpg"
                width="340"
              />
            </picture>
            <div class="card-body text-center">
              <h3
                class="text-uppercase fs-18 mt-3 mb-0"
                style="letter-spacing: 1px"
              >
                Art
              </h3>
            </div>
          </a>
        </div>
      </div>
    </section>
    <!-- End of Category section
       ============================================= -->
  </main>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import HomeTopCollections from "../components/HomeTopCollections.vue";
import BaseButton from "../components/base/BaseButton.vue";
import VueAgile from "vue-agile/src/Agile.vue";

export default defineComponent({
  name: "HomeView",
  components: { BaseButton, HomeTopCollections, agile: VueAgile },
  data() {
    return {
      slickOptions: {
        dots: true,
        infinite: false,
        speed: 300,
        slidesToShow: 4.71,
        slidesToScroll: 2,
        responsive: [
          {
            breakpoint: 1500,
            settings: {
              slidesToShow: 4,
              slidesToScroll: 2,
              infinite: true,
              dots: true,
            },
          },
          {
            breakpoint: 1280,
            settings: {
              slidesToShow: 3,
              slidesToScroll: 3,
              infinite: true,
              dots: true,
            },
          },
          {
            breakpoint: 1024,
            settings: {
              slidesToShow: 2.4,
              slidesToScroll: 2,
            },
          },
          {
            breakpoint: 768,
            settings: {
              slidesToShow: 1.5,
              slidesToScroll: 1,
            },
          },
          {
            breakpoint: 500,
            settings: {
              slidesToShow: 1,
              slidesToScroll: 1,
              dots: true,
            },
          },
          // You can unslick at a given breakpoint now by adding:
          // settings: "unslick"
          // instead of a settings object
        ],
      },
    };
  },
});
</script>

<style>
.agile__dots {
  @apply absolute left-1/2 bottom-0 p-0 mb-4 -translate-x-1/2;
}

.agile__dot {
  @apply mx-2.5 my-0;
}

.agile__dot button {
  @apply block cursor-pointer bg-gray-500 w-2.5 h-2.5 m-0 p-0 duration-300;
  border-radius: 50%;
}

.agile__dot--current button,
.agile__dot:hover button {
  @apply bg-yellow;
}
</style>
