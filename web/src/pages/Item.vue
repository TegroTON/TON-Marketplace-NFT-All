<template>
  <main class="main-page">
    <section class="item-details section pt-5">
      <div class="container-fluid">
        <div class="row justify-content-center">
          <div class="col-md-10 col-lg-12 col-xxl-9">
            <item-breadcrumbs :address="address"></item-breadcrumbs>
            <div class="row justify-content-center mb-4">
              <div class="col-lg-5 col-xl-5 mb-4 mb-lg-0">
                <!-- Start NFT Image -->
                <div class="position-sticky" style="top: 140px;">
                  <enlargeable-image :src="item.image"></enlargeable-image>
                </div>
                <!-- // End NFT Image -->
              </div>
              <div class="col-lg-7 col-xl-7">
                <!-- Start Desc Card -->
                <item-details-card :address="address"></item-details-card>
                <!-- // End Desc Card -->
                <!-- Start Action Link -->
                <div class="card-item-details d-flex align-items-center border bg-soft px-4 py-3 rounded mb-4">
                  <button v-if="false" class="btn btn-sm btn-details__like bg-soft px-3" type="button">
                    <i class="fa-regular fa-heart m-0 me-sm-2"></i>
                    <span class="d-block d-sm-inline mt-1 mt-sm-0">24</span>
                  </button>
                  <div class="nav-item-details d-flex align-items-center fs-18 ms-auto">
                    <button v-if="false" class="btn btn-sm px-2 px-3 me-1" type="button"><i
                        class="fa-regular fa-arrows-rotate d-block d-sm-inline mb-2 mt-sm-0 m-0 me-sm-2"></i> Refresh
                    </button>
                    <button class="btn btn-sm px-2 px-3 me-1" data-bs-target="#ShareModal" data-bs-toggle="modal"
                            type="button">
                      <i class="fa-regular fa-share-nodes d-block d-sm-inline mb-2 mt-sm-0 m-0 me-sm-2"></i> Share
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
                <item-price-card :address="address"></item-price-card>
                <!-- // End Price Card -->
                <!-- Start Owner & Collection -->
                <div class="row">
                  <item-owner-card :address="address"></item-owner-card>
                  <item-collection-card :address="address"></item-collection-card>
                </div>
                <!-- // End Owner & Collection -->
                <!-- Start Details Card -->
                <item-details-card :address="address"></item-details-card>
                <!-- // End Details Card -->
              </div>
            </div>
            <!-- Start Attributes Card -->
            <item-attributes-card :address="address"></item-attributes-card>
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
  <share-modal></share-modal>
  <buy-now-modal :address="address"></buy-now-modal>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import EnlargeableImage from "../components/EnlargeableImage.vue";
import gql from "graphql-tag";
import ItemBreadcrumbs from "../components/item/ItemBreadcrumbs.vue";
import ItemAttributesCard from "../components/item/ItemAttributesCard.vue";
import ItemDetailsCard from "../components/item/ItemDetailsCard.vue";
import ItemOwnerCard from "../components/item/ItemOwnerCard.vue";
import ItemCollectionCard from "../components/item/ItemCollectionCard.vue";
import ShareModal from "../modals/ShareModal.vue";
import ItemPriceCard from "../components/item/ItemPriceCard.vue";
import BuyNowModal from "../modals/BuyNowModal.vue";

export default defineComponent({
  name: "Item",
  components: {
    BuyNowModal,
    ItemPriceCard,
    ShareModal,
    ItemCollectionCard,
    ItemOwnerCard, ItemDetailsCard, ItemAttributesCard, ItemBreadcrumbs, EnlargeableImage
  },
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
          address
          image
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
        address: this.address,
        image: null as string | null,
      }
    }
  },
})
</script>
