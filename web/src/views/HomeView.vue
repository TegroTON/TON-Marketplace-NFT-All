<template>
  <main class="main-page">
    <!-- Start of Hero section
       ============================================= -->
    <section id="hero" class="hero section">
      <div id="carouselHeroIndicators" class="hero__container container-fluid carousel slide p-0"
           data-bs-interval="false" data-ride="carousel">
        <div class="carousel-indicators">
          <button aria-current="true" aria-label="Slide 1" class="active" data-bs-slide-to="0"
                  data-bs-target="#carouselHeroIndicators" type="button"></button>
          <button aria-label="Slide 2" data-bs-slide-to="1" data-bs-target="#carouselHeroIndicators"
                  type="button"></button>
        </div>
        <div class="carousel-inner">
          <div class="carousel-item active">
            <div class="row alight-items-center">
              <div class="col-md-12 col-lg-9 col-xl-9 col-xxl-7 text-center text-md-start">
                <h1 class="hero__title">
                  Discover the Exclusive <span class="d-block">NFT Collection</span>
                </h1>
                <div class="hero__desc">
                  Click My Collections and set up your collection. <br>
                  Add social links, a description, profile & banner images, <br> and set a secondary sales fee.
                </div>
                <a class="btn btn-outline-primary" href="#!">
                  <span>Learn more</span>
                </a>
              </div>
            </div>
            <picture>
              <source srcset="../assets/hero-image-1.webp" type="image/webp">
              <source srcset="../assets/hero-image-1.jpg" type="image/jpeg">
              <img alt="Libermall NFT Marketplace" class="hero__image" src="../assets/hero-image-1.jpg">
            </picture>
          </div>
          <div class="carousel-item">
            <div class="row alight-items-center">
              <div class="col-md-12 col-lg-9 col-xl-9 col-xxl-7 text-center text-md-start">
                <h1 class="hero__title">
                  Discover the Exclusive <span class="d-block">NFT Collection</span>
                </h1>
                <div class="hero__desc">
                  Click My Collections and set up your collection.
                  <span class="d-inline d-lg-block">Add social links, a description, profile & banner images,</span> and
                  set a secondary sales fee.
                </div>
                <a class="btn btn-outline-primary" href="#!">
                  <span>Learn more</span>
                </a>
              </div>
            </div>
            <picture>
              <source srcset="../assets/hero-image-2.webp" type="image/webp">
              <source srcset="../assets/hero-image-2.jpg" type="image/jpeg">
              <img alt="Libermall NFT Marketplace" class="hero__image" loading="lazy" src="../assets/hero-image-2.jpg">
            </picture>
          </div>
        </div>
        <button class="carousel-control-prev  d-none d-lg-block" data-bs-slide="prev"
                data-bs-target="#carouselHeroIndicators"
                type="button">
          <i class="fa-solid fa-arrow-left"></i>
        </button>
        <button class="carousel-control-next  d-none d-lg-block" data-bs-slide="next"
                data-bs-target="#carouselHeroIndicators"
                type="button">
          <i class="fa-solid fa-arrow-right"></i>
        </button>
      </div>
    </section>
    <!-- End of Hero section
       ============================================= -->
    <!-- Start of Discover section
       ============================================= -->
    <section id="discover" class="discover section pt-150">
      <div class="discover__container container-fluid text-center">
        <h2 class="section__title mb-3">
          Discover, Collect and sell <span class="color-yellow">extraordinary NFTs</span>
        </h2>
        <p class="section__desc mb-5">
          Libermall is the world's first and largest NFT marketplace
        </p>
        <div class="d-flex flex-wrap align-items-center justify-content-center">
          <a class="btn btn-primary m-3" href="/explore.php">
            Explore
          </a>
          <a class="btn btn-outline-primary m-3" href="#!">
            + Create
          </a>
        </div>
      </div>
    </section>
    <!-- End of Discover section
       ============================================= -->
    <!-- Start of Libermall section
       ============================================= -->
    <section id="Libermall" class="libermall section">
      <div class="libermall__container container-fluid">
        <h2 class="section__title mb-3">
          TON is in beta <span class="d-block d-md-inline">on Libermall</span>
        </h2>
        <div class="libermall__buttons d-flex align-items-center">
          <a class="btn btn-dark" href="#!">Explore</a>
          <div class="libermall__soclinks ms-5 d-none d-md-flex">
            <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-telegram"></i></a>
            <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-discord"></i></a>
            <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-twitter"></i></a>
            <a class="libermall__soclinks-item" href="#!"><i class="fa-brands fa-instagram"></i></a>
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
        <slick-carousel :options="slickOptions" class="notable-slider pt-3">
          <a class="card card-gradient top-effect" href="/collection.php" title="TODO: Name">
            <div class="card__bage color-yellow text-uppercase">
              <i class="fa-solid fa-fire-flame-curved me-1"></i>
              Live
            </div>
            <picture>
              <source srcset="./assets/img/nft/nft-1.webp" type="image/webp">
              <source srcset="./assets/img/nft/nft-1.jpg" type="image/jpeg">
              <img alt="TODO: Name" class="rounded card-img" height="390" loading="lazy"
                   src="./assets/img/nft/nft-1.jpg"
                   width="300">
            </picture>
            <div class="card-body">
              <h3 class="fs-20 my-3">TODO: Name</h3>
              <p class="d-flex align-items-center mb-0 color-grey">
                <span class="icon-ton me-2"></span>
                floor price: 0.5
              </p>
            </div>
          </a>
        </slick-carousel>
      </div>
    </section>
    <!-- End of Notable section
       ============================================= -->
    <!-- Start of Collections section
       ============================================= -->
    <section class="collections section">
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
              <img alt="" class="create-card__img" height="100px" src="../assets/create-card-1.png" width="100px">
            </div>
            <div class="create-card__body ms-4">
              <div class="create-card__num color-grey mb-3">
                0<span class="color-yellow">1</span>
              </div>
              <h4 class="fs-24 mb-4">
                Set up your wallet
              </h4>
              <p class="fs-18 color-grey mb-0">
                Once you’ve set up your wallet of choice, connect it to Libermall by clicking the wallet icon in the top
                right corner. Learn about the wallets we support
              </p>
            </div>
          </div>
          <div class="create-card d-flex">
            <div class="create-card__icon d-none d-md-block">
              <img alt="" class="create-card__img" height="100px" src="../assets/create-card-2.png" width="100px">
            </div>
            <div class="create-card__body ms-4">
              <div class="create-card__num color-grey mb-3">
                0<span class="color-yellow">2</span>
              </div>
              <h4 class="fs-24 mb-4">
                Create your collection
              </h4>
              <p class="fs-18 color-grey mb-0">
                Once you’ve set up your wallet of choice, connect it to Libermall by clicking the wallet icon in the top
                right corner. Learn about the wallets we support
              </p>
            </div>
          </div>
          <div class="create-card d-flex">
            <div class="create-card__icon d-none d-md-block">
              <img alt="" class="create-card__img" height="90px" src="../assets/create-card-3.png" width="90px">
            </div>
            <div class="create-card__body ms-4">
              <div class="create-card__num color-grey mb-3">
                0<span class="color-yellow">3</span>
              </div>
              <h4 class="fs-24 mb-4">
                Add your NFTs
              </h4>
              <p class="fs-18 color-grey mb-0">
                Once you’ve set up your wallet of choice, connect it to Libermall by clicking the wallet icon in the top
                right corner. Learn about the wallets we support
              </p>
            </div>
          </div>
          <div class="create-card d-flex">
            <div class="create-card__icon d-none d-md-block">
              <img alt="" class="create-card__img" height="90px" src="../assets/create-card-4.png" width="90px">
            </div>
            <div class="create-card__body ms-4">
              <div class="create-card__num color-grey mb-3">
                0<span class="color-yellow">4</span>
              </div>
              <h4 class="fs-24 mb-4">
                List them for sale
              </h4>
              <p class="fs-18 color-grey mb-0">
                Once you’ve set up your wallet of choice, connect it to Libermall by clicking the wallet icon in the top
                right corner. Learn about the wallets we support
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
        <slick-carousel :options="slickOptions" class="category-slider pt-3">
          <a class="card top-effect" href="/single-collection.php">
            <picture>
              <source srcset="./assets/img/category/cat-1.webp" type="image/webp">
              <source srcset="./assets/img/category/cat-1.jpg" type="image/jpeg">
              <img alt="Ragnarok" class="rounded image-300x300" height="275" loading="lazy"
                   src="./assets/img/category/cat-1.jpg" width="340">
            </picture>
            <div class="card-body text-center">
              <h3 class="text-uppercase fs-18 mt-3 mb-0" style="letter-spacing: 1px;">Art</h3>
            </div>
          </a>
        </slick-carousel>
      </div>
    </section>
    <!-- End of Category section
       ============================================= -->
  </main>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import SlickCarousel from "../components/SlickCarousel.vue";
import HomeTopCollections from "../components/HomeTopCollections.vue";

export default defineComponent({
  name: "HomeView",
  components: {HomeTopCollections, SlickCarousel},
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
              dots: true
            }
          },
          {
            breakpoint: 1280,
            settings: {
              slidesToShow: 3,
              slidesToScroll: 3,
              infinite: true,
              dots: true
            }
          },
          {
            breakpoint: 1024,
            settings: {
              slidesToShow: 2.4,
              slidesToScroll: 2
            }
          },
          {
            breakpoint: 768,
            settings: {
              slidesToShow: 1.5,
              slidesToScroll: 1
            }
          },
          {
            breakpoint: 500,
            settings: {
              slidesToShow: 1,
              slidesToScroll: 1,
              dots: true
            }
          }
          // You can unslick at a given breakpoint now by adding:
          // settings: "unslick"
          // instead of a settings object
        ]
      },
    }
  },
})
</script>

<style lang="scss">
@import "../scss/slick";
@import "../scss/slick-theme";
</style>
