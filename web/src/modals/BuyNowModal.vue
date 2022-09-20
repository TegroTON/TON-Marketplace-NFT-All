<template>
  <div id="BuyNowModal" aria-hidden="true" aria-labelledby="BuyNowModalLabel" class="modal fade" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-4">
          <h5 id="BuyNowModalLabel" class="modal-title fs-24">Buy NFT</h5>
          <button aria-label="Close" class="border-0 p-0 modal-close" data-bs-dismiss="modal" type="button"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body mb-3">
          <div class="d-flex align-items-center bg-soft hover rounded p-2 mb-3">
            <img :src=itemImage alt="Tergo Cat" class="rounded image-80x80">
            <div class="__body ms-3">
              <h5 class="fs-18 mb-2">{{ itemDisplayName }}</h5>
              <p class="mb-0 color-grey fs-14">{{ collectionName }}</p>
            </div>
          </div>
          <ul class="list-unstyled mb-3 px-1">
            <li class="d-flex align-items-center mb-1">
              <span>NFT Price</span>
              <span class="ms-auto">{{ formattedFullPrice }}</span>
            </li>
            <li class="d-flex align-items-center fs-14 color-grey mb-1">
              <span>Creator Royalties</span>
              <span class="ms-auto">{{ formattedRoyalty }}</span>
            </li>
            <li class="d-flex align-items-center fs-14 color-grey mb-3">
              <span>Service Fee</span>
              <span class="ms-auto">{{ formattedMarketplaceFee }}</span>
            </li>
            <li class="d-flex align-items-center mb-1">
              <span>Network Fee</span>
              <span class="ms-auto">{{ formattedNetworkFee }}</span>
            </li>
            <li class="d-flex align-items-center fs-14 color-grey">
              <span>The rest will be returned to your wallet</span>
            </li>
          </ul>
          <div class="d-flex align-items-center bg-soft border rounded fs-18 fw-medium p-3 mb-3">
            <span>You Pay</span>
            <span class="ms-auto">{{ formattedBuyPrice }}</span>
          </div>
          <div class="alert alert-warning" role="alert">
            <h4 class="alert-heading fs-16">Well done!</h4>
            <p class="mb-2 fs-14">
              Libermall is unaffiliated with any NFT projects. We are not responsible for possible losses. Invest at
              your own risk.
            </p>
            <a v-if="false" class="color-yellow" href="#!">Learn More</a>
          </div>
        </div>
        <button :disabled="!item.isOnSale" class="btn btn-primary w-100" data-bs-target="#ConnectModal"
                data-bs-toggle="modal" type="button">
          Buy for {{ formattedBuyPrice }}
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import gql from "graphql-tag";
import {formatPrice} from "../utility";
import defaultImage from "../../assets/img/user-1.svg";


export default defineComponent({
  name: "BuyNowModal",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  apollo: {
    item: {
      query: gql`query item($address: String!) {
        item(address: $address) {
          address
          index
          name
          image
          isOnSale
          fullPrice
          royaltyAmount
          marketplaceFee
          networkFee
          buyPrice
          collection {
            address
            name
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
        address: this.address,
        index: "0",
        name: null as string | null,
        image: null as string | null,
        isOnSale: false,
        fullPrice: "0",
        royaltyAmount: "0",
        marketplaceFee: "0",
        networkFee: "0",
        buyPrice: "0",
        collection: null as {
          address: string,
          name: string | null
        } | null,
      }
    }
  },
  computed: {
    itemImage() {
      if (this.item.image !== null) {
        return this.item.image
      } else {
        return defaultImage
      }
    },
    itemDisplayName() {
      return this.item.name ?? ("Item no. " + this.item.index)
    },
    collectionName() {
      return this.item.collection?.name ?? "Untitled Collection"
    },
    formattedFullPrice() {
      return formatPrice(this.item.fullPrice)
    },
    formattedRoyalty() {
      return formatPrice(this.item.royaltyAmount)
    },
    formattedMarketplaceFee() {
      return formatPrice(this.item.marketplaceFee)
    },
    formattedNetworkFee() {
      return formatPrice(this.item.networkFee)
    },
    formattedBuyPrice() {
      return formatPrice(this.item.buyPrice)
    },

  },
})
</script>
