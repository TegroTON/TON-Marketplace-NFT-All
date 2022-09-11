<template>
  <section class="nft-hero">
    <picture>
      <source srcset="assets/img/profile-hero.webp" type="image/webp">
      <source srcset="assets/img/profile-hero.jpg" type="image/jpeg">
      <img alt="Tegro Cat" class="nft-hero__image" height="275" loading="lazy" src="assets/img/profile-hero.jpg"
           width="340">
    </picture>
  </section>
  <main class="main-page">
    <section class="nfr-collection section pt-0">
      <div class="container-fluid">
        <div class="row">
          <div class="col-lg-4 col-xl-4 col-xxl-3 mb-4 mb-lg-0">
            <!-- Start Collection Info -->
            <div class="card card-blur p-0 mt--100">
              <div class="card__share d-flex position-absolute" style="top: 8px; right: 8px">
                <button class="btn btn-cube" data-bs-target="#ShareModal" data-bs-toggle="modal" type="button"><i
                    class="fa-regular fa-share-nodes"></i></button>
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
                    <img alt="" class="img-fluid rounded-circle" height="80" src="assets/img/cats/t-cat-02.jpg"
                         width="80">
                  </div>
                  <div class="d-flex mx-0 mx-lg-auto">
                    <a class="btn btn-sm btn-outline-primary me-3" href="#!">Follow</a>
                    <a class="btn btn-sm btn-soft" data-bs-target="#MessageModal" data-bs-toggle="modal"
                       data-bs-whatever="@Tegro"
                       href="#!">Message</a>
                  </div>
                </div>
                <h1 class="collection__name fs-24 mb-4">
                  <span>Tegro</span>
                  <i class="fa-solid fa-circle-check fs-22 color-yellow ms-2"></i>
                </h1>
                <div class="collection__desc color-grey mb-4">
                  <p>
                    The first exclusive NFTs on The Open Network about resistance dogs from Tegro.
                  </p>
                  <a class="collection__link text-white" href="#!" target="__blank">
                    <i class="fa-regular fa-link-simple color-yellow me-2"></i>
                    tegro.com
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
            </div>
            <!-- End Start Collection Info -->
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
                  <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">1.3K</p>
                </div>
                <div class="card-blur__item p-4 border-end text-center">
                  <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">Owners</h5>
                  <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">461</p>
                </div>
                <div class="card-blur__item p-4 border-end text-center">
                  <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">Blockchain</h5>
                  <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">TON Conin</p>
                </div>
                <div class="card-blur__item p-4 text-center">
                  <h5 class="text-uppercase fs-14 color-grey" style="letter-spacing: 1px;">address</h5>
                  <p class="m-0 text-uppercase fw-medium" style="letter-spacing: 1px;">0x15f...d860</p>
                </div>
              </div>
            </div>
            <!-- End Collection Stat -->
            <!-- Start Collection Stat -->
            <div id="mouseScroll" class="overflow-auto mobile__nav-bottom mb-4 px-2">
              <ul id="myTab" class="nav collections__nav list-unstyled d-flex flex-nowrap align-items-center"
                  role="tablist">
                <li class="collections__nav-item">
                  <button id="Owned-tab" aria-controls="Owned"
                          aria-selected="true"
                          class="collections__nav-link d-flex align-items-center text-nowrap active"
                          data-bs-target="#Owned" data-bs-toggle="tab" role="tab"
                          type="button">Owned <span class="badge bg-grey ms-2">25</span></button>
                </li>
                <li class="collections__nav-item ">
                  <button id="Onsale-tab" aria-controls="Onsale"
                          aria-selected="false" class="collections__nav-link d-flex align-items-center text-nowrap"
                          data-bs-target="#Onsale" data-bs-toggle="tab" role="tab"
                          type="button">On sale <span class="badge bg-grey ms-2">24</span></button>
                </li>
                <li class="collections__nav-item">
                  <button id="Created-tab" aria-controls="Created"
                          aria-selected="false" class="collections__nav-link d-flex align-items-center text-nowrap"
                          data-bs-target="#Created" data-bs-toggle="tab"
                          role="tab" type="button">Created <span
                      class="badge bg-grey ms-2">24</span></button>
                </li>
                <li class="collections__nav-item">
                  <button id="Activity-tab" aria-controls="Activity"
                          aria-selected="false" class="collections__nav-link d-flex align-items-center text-nowrap"
                          data-bs-target="#Activity" data-bs-toggle="tab"
                          role="tab" type="button">Activity
                  </button>
                </li>
                <li class="collections__nav-item">
                  <button id="Sold-tab" aria-controls="Sold"
                          aria-selected="false" class="collections__nav-link d-flex align-items-center text-nowrap"
                          data-bs-target="#Sold" data-bs-toggle="tab" role="tab"
                          type="button">Sold <span class="badge bg-grey ms-2">11</span></button>
                </li>
                <li class="ms-0 ms-md-autp ms-lg-0 ms-xl-auto">
                  <select aria-label="Select Category" class="form-select">
                    <option selected>Recently listed</option>
                    <option value="1">Price: low to high</option>
                    <option value="2">Price: high to low</option>
                    <option value="3">Auction ending soon</option>
                  </select>
                </li>
              </ul>
            </div>
            <div id="myTabContent" class="tab-content py-4">
              <div id="Owned" aria-labelledby="Owned-tab" class="tab-pane fade show active" role="tabpanel">
                <collection-list></collection-list>
              </div>
              <div id="Onsale" aria-labelledby="Onsale-tab" class="tab-pane fade" role="tabpanel">
                <collection-list></collection-list>
              </div>
              <div id="Created" aria-labelledby="Created-tab" class="tab-pane fade" role="tabpanel">
                <collection-list></collection-list>
              </div>
              <div id="Created" aria-labelledby="Created-tab" class="tab-pane fade" role="tabpanel">
                <collection-list></collection-list>
              </div>
              <div id="Activity" aria-labelledby="Activity-tab" class="tab-pane fade" role="tabpanel">
                <collection-list></collection-list>
              </div>
              <div id="Sold" aria-labelledby="Sold-tab" class="tab-pane fade" role="tabpanel">
                <collection-list></collection-list>
              </div>
            </div>
            <!-- End Collection Stat -->
            <button class="btn btn-outline-secondary mt-3 w-100" type="button">LOAD MORE</button>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import CollectionFilters from "../components/CollectionFilters.vue";
import CollectionList from "../components/CollectionList.vue";

export default defineComponent({
  name: "Profile",
  components: {CollectionList, CollectionFilters}
})
</script>
