<template>
  <main class="main-page">
    <section class="item-details section pt-5">
      <div class="container-fluid">
        <div class="row justify-content-center">
          <div class="col-md-10 col-lg-12 col-xxl-9">
            <nav aria-label="breadcrumb">
              <ol class="breadcrumb mb-5 ms-3">
                <li class="breadcrumb-item">
                  <router-link :to="{name: 'explore'}">Explore</router-link>
                </li>
                <li v-if="item.contract.collection !== null" class="breadcrumb-item">
                  <router-link :to="{name: 'collection', params: {address: item.contract.collection.address}}">
                    {{ item.contract.collection.metadata.name ?? "Collection" }}
                  </router-link>
                </li>
                <li aria-current="page" class="breadcrumb-item active">
                  {{ itemDisplayName }}
                </li>
              </ol>
            </nav>
            <div class="row justify-content-center mb-4">
              <div class="col-lg-5 col-xl-5 mb-4 mb-lg-0">
                <!-- Start NFT Image -->
                <div class="position-sticky" style="top: 140px;">
                  <enlargeable-image :src="item.metadata.image"></enlargeable-image>
                </div>
                <!-- // End NFT Image -->
              </div>
              <div class="col-lg-7 col-xl-7">
                <!-- Start Desc Card -->
                <div class="card-item-details mb-5">
                  <div v-if="item.sale === null" class="item-details__badge mb-4">Not for sale</div>
                  <div v-else class="item-details__badge badge__green mb-4">For Sale</div>
                  <h1 class="item-details__title mb-3">{{ itemDisplayName }}</h1>
                  <p class="item-details__desc mb-0"> {{ item.metadata.description }} </p>
                </div>
                <!-- // End Desc Card -->
                <!-- Start Action Link -->
                <div class="card-item-details d-flex align-items-center border bg-soft px-4 py-3 rounded mb-4">
                  <button v-if="false" class="btn btn-sm btn-details__like bg-soft px-3" type="button">
                    <i class="fa-regular fa-heart m-0 me-sm-2"></i>
                    <span class="d-block d-sm-inline mt-1 mt-sm-0">24</span>
                  </button>
                  <div v-if="false" class="nav-item-details d-flex align-items-center fs-18 ms-auto">
                    <button v-if="false" class="btn btn-sm px-2 px-3 me-1" type="button"><i
                        class="fa-regular fa-arrows-rotate d-block d-sm-inline mb-2 mt-sm-0 m-0 me-sm-2"></i> Refresh
                    </button>
                    <button v-if="false" class="btn btn-sm px-2 px-3 me-1" data-bs-target="#ShareModal"
                            data-bs-toggle="modal"
                            type="button"><i
                        class="fa-regular fa-share-nodes d-block d-sm-inline mb-2 mt-sm-0 m-0 me-sm-2"></i> Share
                    </button>
                    <div v-if="false" class="dropdown">
                      <button id="dropdownMenuButton1" aria-expanded="false" class="btn btn-sm btn-cube"
                              data-bs-toggle="dropdown" type="button">
                        <i class="fa-solid fa-ellipsis fs-20"></i>
                      </button>
                      <ul aria-labelledby="dropdownMenuButton1" class="dropdown-menu">
                        <li><a class="dropdown-item" href="#">Place floor bid</a></li>
                        <li><a class="dropdown-item" href="#">New bid</a></li>
                        <li><a class="dropdown-item" href="#">Report</a></li>
                      </ul>
                    </div>
                  </div>
                </div>
                <!-- // End Action Link -->
                <!-- Start Price Card -->
                <div v-if="item.sale !== null" class="card-item-details border bg-soft p-4 rounded mb-4">
                  <div class="d-flex align-items-center mb-2">
                    <span class="d-block text-uppercase fs-18 fw-medium">Price:</span>
                    <span class="price-item__ton d-block ms-auto">{{ itemPrice }} TON</span>
                  </div>
                  <div class="d-flex align-items-center mb-4">
                    <span class="color-grey">Plus a network fee of 1 TON</span>
                    <span v-if="false" class="price-item__dollar d-block color-grey text-end ms-auto">$64.09</span>
                  </div>
                  <button v-if="false" class="btn btn-primary w-100" data-bs-target="#BuyNowModal"
                          data-bs-toggle="modal"
                          type="button">Buy Now
                  </button>
                </div>
                <!-- // End Price Card -->
                <!-- Start Owner & Collection -->
                <div class="row">
                  <div class="col-xl-6 mx-auto mb-4">
                    <!--                    TODO: Link-->
                    <div class="card-item-details d-block border bg-soft hover px-4 py-3 rounded text-white">
                      <h4 class="fs-14 color-grey mb-4">Owner</h4>
                      <div class="d-flex align-items-center">
                        <img alt="" class="img-fluid rounded-circle" height="40" src="assets/img/author/author-12.jpg"
                             width="40">
                        <h4 class="collection__name fs-16 mb-0 ms-3">
                          {{ ownerAddress }}
                        </h4>
                        <i v-if="false" class="fa-solid fa-angle-right color-grey ms-auto"></i>
                      </div>
                    </div>
                  </div>
                  <div v-if="item.contract.collection !== null" class="col-xl-6 mb-4">
                    <router-link :to="{name: 'collection', params: {address: item.contract.collection.address}}"
                                 class="card-item-details d-block border bg-soft hover px-4 py-3 rounded text-white">
                      <h4 class="fs-14 color-grey mb-4">Collection</h4>
                      <div class="d-flex align-items-center">
                        <img :src="item.contract.collection.metadata?.image" alt="" class="img-fluid rounded-circle"
                             width="40">
                        <h4 class="collection__name fs-16 mb-0 ms-3">
                          {{ item.contract.collection.metadata?.name }}
                        </h4>
                        <i class="fa-solid fa-angle-right color-grey ms-auto"></i>
                      </div>
                    </router-link>
                  </div>
                </div>
                <!-- // End Owner & Collection -->
                <!-- Start Details Card -->
                <div class="card-item-details border bg-soft p-4 rounded">
                  <h4 class="fs-18 mb-4">Details</h4>
                  <ul class="list-unstyled">
                    <li class="mb-3">
                      <a :href="'https://testnet.tonscan.org/address/' + itemAddress"
                         class="d-flex align-items-center p-3 rounded border hover text-white"
                         target="_blank">
                        <span class="fw-medium color-grey">Contract Address</span>
                        <span
                            class="col-4 text-truncate ms-auto">{{ itemAddress }}</span>
                        <i class="fa-solid fa-angle-right color-grey ms-3"></i>
                      </a>
                    </li>
                    <li v-if="false" class="mb-3">
                      <a class="d-flex align-items-center p-3 rounded border hover text-white"
                         href="https://tonscan.org/address/EQC_-u5FytW3WrGR_UQ_tjVFvFbcIanSh4nHjqP3ojIamkGP"
                         target="_blank">
                        <span class="fw-medium color-grey">Sale Contract</span>
                        <span
                            class="col-4 text-truncate ms-auto">EQC_-u5FytW3WrGR_UQ_tjVFvFbcIanSh4nHjqP3ojIamkGP</span>
                        <i class="fa-solid fa-angle-right color-grey ms-3"></i>
                      </a>
                    </li>
                    <li class="d-flex align-items-center p-3 rounded border hover text-white mb-3">
                      <span class="fw-medium color-grey">Metadata</span>
                      <span class="text-truncate ms-auto">Centralized</span>
                    </li>
                  </ul>
                </div>
                <!-- // End Details Card -->
              </div>
            </div>
            <!-- Start Attributes Card -->
            <div v-if="false" class="card-item-details border bg-soft p-4 rounded mb-5">
              <h4 class="fs-18 mb-4">Attributes</h4>
              <ul class="row list-unstyled">
                <li class="col-lg-6 col-xl-4 mb-3">
                  <a class="d-flex align-items-center p-3 rounded border hover text-white" href="#!" target="_blank">
                    <div>
                      <span class="d-block fs-14 fw-medium color-grey mb-2">Background</span>
                      <span class="d-block fw-medium">Orange</span>
                    </div>
                    <div class="ms-auto">
                      <span class="d-block fs-14 fw-medium color-grey text-end mb-2">Rarity</span>
                      <div class="d-flex fs-14">
                        <span>22/100</span>
                        <span class="mx-1">~</span>
                        <span class="fw-medium text-end">17.1%</span>
                      </div>
                    </div>
                    <i class="fa-solid fa-angle-right color-grey ms-5"></i>
                  </a>
                </li>
                <li class="col-lg-6 col-xl-4 mb-3">
                  <a class="d-flex align-items-center p-3 rounded border hover text-white" href="#!" target="_blank">
                    <div>
                      <span class="d-block fs-14 fw-medium color-grey mb-2">Expression</span>
                      <span class="d-block fw-medium">Brown Eyes</span>
                    </div>
                    <div class="ms-auto">
                      <span class="d-block fs-14 fw-medium color-grey text-end mb-2">Rarity</span>
                      <div class="d-flex fs-14">
                        <span>22/100</span>
                        <span class="mx-1">~</span>
                        <span class="fw-medium text-end">17.1%</span>
                      </div>
                    </div>
                    <i class="fa-solid fa-angle-right color-grey ms-5"></i>
                  </a>
                </li>
                <li class="col-lg-6 col-xl-4 mb-3">
                  <a class="d-flex align-items-center p-3 rounded border hover text-white" href="#!" target="_blank">
                    <div>
                      <span class="d-block fs-14 fw-medium color-grey mb-2">Hairstyle</span>
                      <span class="d-block fw-medium">Double Ponies</span>
                    </div>
                    <div class="ms-auto">
                      <span class="d-block fs-14 fw-medium color-grey text-end mb-2">Rarity</span>
                      <div class="d-flex fs-14">
                        <span>22/100</span>
                        <span class="mx-1">~</span>
                        <span class="fw-medium text-end">17.1%</span>
                      </div>
                    </div>
                    <i class="fa-solid fa-angle-right color-grey ms-5"></i>
                  </a>
                </li>
                <li class="col-lg-6 col-xl-4 mb-3">
                  <a class="d-flex align-items-center p-3 rounded border hover text-white" href="#!" target="_blank">
                    <div>
                      <span class="d-block fs-14 fw-medium color-grey mb-2">Outfit</span>
                      <span class="d-block fw-medium">Qipao</span>
                    </div>
                    <div class="ms-auto">
                      <span class="d-block fs-14 fw-medium color-grey text-end mb-2">Rarity</span>
                      <div class="d-flex fs-14">
                        <span>22/100</span>
                        <span class="mx-1">~</span>
                        <span class="fw-medium text-end">17.1%</span>
                      </div>
                    </div>
                    <i class="fa-solid fa-angle-right color-grey ms-5"></i>
                  </a>
                </li>
                <li class="col-lg-6 col-xl-4 mb-3">
                  <a class="d-flex align-items-center p-3 rounded border hover text-white" href="#!" target="_blank">
                    <div>
                      <span class="d-block fs-14 fw-medium color-grey mb-2">Prop</span>
                      <span class="d-block fw-medium">Lolipop</span>
                    </div>
                    <div class="ms-auto">
                      <span class="d-block fs-14 fw-medium color-grey text-end mb-2">Rarity</span>
                      <div class="d-flex fs-14">
                        <span>22/100</span>
                        <span class="mx-1">~</span>
                        <span class="fw-medium text-end">17.1%</span>
                      </div>
                    </div>
                    <i class="fa-solid fa-angle-right color-grey ms-5"></i>
                  </a>
                </li>
              </ul>
            </div>
            <!-- // End Attributes Card -->

            <!-- Start Transaction Table -->
            <h4 v-if="false" class="mb-3 fs-28 px-2">Transaction History</h4>
            <div v-if="false" class="card-item-details border bg-soft rounded mt-4 table-responsive">
              <table class="table text-white mb-0">
                <thead>
                <tr class="border-bottom">
                  <th class="p-4" scope="col">Type</th>
                  <th class="p-4" scope="col">Price</th>
                  <th class="p-4" scope="col">From</th>
                  <th class="p-4" scope="col">To</th>
                  <th class="p-4" scope="col">Date</th>
                </tr>
                </thead>
                <tbody>
                <tr class="hover">
                  <td class="p-4">
                    <i class="fa-regular fa-cart-shopping-fast fs-18 me-2"></i>
                    <span class="fw-medium">Sale</span>
                  </td>
                  <td class="p-4">38 TON</td>
                  <td class="p-4">
                    <a class="d-block text-truncate" href="/profile.php" style="max-width: 150px;" target="_blank">
                      <i class="fa-regular fa-circle-user me-2"></i>EQDZr7KDKG0R4Kyauz-iRetnuY7nMKXFIEQn4-44vzygNEvj
                    </a>
                  </td>
                  <td class="p-4">
                    <a class="d-block text-truncate" href="/profile.php" style="max-width: 150px;" target="_blank">
                      <i class="fa-regular fa-circle-user me-2"></i>antonov
                    </a>
                  </td>
                  <td class="p-4"><i class="fa-regular fa-timer me-2"></i> 2 hours ago</td>
                </tr>
                <tr class="hover">
                  <td class="p-4">
                    <i class="fa-regular fa-arrow-right-arrow-left me-2"></i>
                    <span class="fw-medium">Transfer</span>
                  </td>
                  <td class="p-4"></td>
                  <td class="p-4">
                    <a class="d-block text-truncate" href="/profile.php" style="max-width: 150px;" target="_blank">
                      <i class="fa-regular fa-circle-user me-2"></i>EQDZr7KDKG0R4Kyauz-iRetnuY7nMKXFIEQn4-44vzygNEvj
                    </a>
                  </td>
                  <td class="p-4">
                    <a class="d-block text-truncate" href="/profile.php" style="max-width: 150px;" target="_blank">
                      <i class="fa-regular fa-circle-user me-2"></i>antonov
                    </a>
                  </td>
                  <td class="p-4"><i class="fa-regular fa-timer me-2"></i> 12 hours ago</td>
                </tr>
                <tr class="hover">
                  <td class="p-4">
                    <i class="fa-regular fa-coin-blank me-2"></i>
                    <span class="fw-medium">Mint</span>
                  </td>
                  <td class="p-4">40 TON</td>
                  <td class="p-4">
                    <a class="d-block text-truncate" href="/profile.php" style="max-width: 150px;" target="_blank">
                      <i class="fa-regular fa-circle-user me-2"></i>EQDZr7KDKG0R4Kyauz-iRetnuY7nMKXFIEQn4-44vzygNEvj
                    </a>
                  </td>
                  <td class="p-4">
                    <a class="d-block text-truncate" href="/profile.php" style="max-width: 150px;" target="_blank">
                      <i class="fa-regular fa-circle-user me-2"></i>antonov
                    </a>
                  </td>
                  <td class="p-4"><i class="fa-regular fa-timer me-2"></i> 1 day ago</td>
                </tr>
                </tbody>
              </table>
            </div>
            <!-- //End Transaction Table -->
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import EnlargeableImage from "../components/EnlargeableImage.vue";
import gql from "graphql-tag";
import {Address, fromNano} from "ton";
import normalizeAndShorten from "../utility";

export default defineComponent({
  name: "Item",
  components: {EnlargeableImage},
  props: {
    address: {
      type: String,
      required: true,
    },
  },
  apollo: {
    item: {
      query: gql`query item($address: String!) {
        item(address: $address) {
          contract {
            index
            owner
            collection {
              address
              metadata {
                name
                image
              }
            }
          }
          metadata {
            name
            description
            image
          }
          sale {
            fullPrice
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
      item: {
        contract: {
          index: "0",
          owner: null as string | null,
          collection: null as null | {
            address: string,
            metadata: null | {
              name: null | string,
              image: null | string,
            }
          }
        },
        metadata: null as null | {
          name: string | null,
          description: string | null,
          image: string | null,
        },
        sale: null as null |
            {
              fullPrice: string
            },
      }
    }
  },
  computed: {
    itemDisplayName: function () {
      return this.item.metadata?.name ?? ("Item no. " + this.item.contract.index)
    },
    itemAddress: function () {
      return Address.parse(this.address).toFriendly({urlSafe: true, bounceable: true, testOnly: true})
    },
    ownerAddress: function () {
      if (this.item.contract.owner !== null)
        return normalizeAndShorten(this.item.contract.owner)
      else
        return ""
    },
    itemPrice: function () {
      if (this.item.sale !== null)
        return fromNano(this.item.sale.fullPrice)
      else
        return null
    }
  }
})
</script>
