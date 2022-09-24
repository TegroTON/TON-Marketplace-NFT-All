<template>
  <item-layout>
    <template #breadcrumb>
      <item-breadcrumb :address="address"></item-breadcrumb>
    </template>
    <template #content>
      <item-content :address="address"></item-content>
    </template>
    <template #details>
      <!-- Start Desc Card -->
      <item-description :address="address"></item-description>
      <!-- // End Desc Card -->
      <!-- Start Action Link -->
      <div class="card-item-details d-flex align-items-center border bg-soft px-4 py-3 rounded mb-4">
        <button class="btn btn-sm btn-details__like bg-soft px-3" type="button">
          <i class="fa-regular fa-heart m-0 me-sm-2"></i>
          <span class="d-block d-sm-inline mt-1 mt-sm-0">24</span>
        </button>
        <div class="nav-item-details d-flex align-items-center fs-18 ms-auto">
          <button class="btn btn-sm px-2 px-3 me-1" type="button"><i
              class="fa-regular fa-arrows-rotate d-block d-sm-inline mb-2 mt-sm-0 m-0 me-sm-2"></i> Refresh
          </button>
          <button class="btn btn-sm px-2 px-3 me-1" data-bs-target="#ShareModal" data-bs-toggle="modal" type="button"><i
              class="fa-regular fa-share-nodes d-block d-sm-inline mb-2 mt-sm-0 m-0 me-sm-2"></i> Share
          </button>
          <div class="dropdown">
            <button id="dropdownMenuButton1" aria-expanded="false" class="btn btn-sm btn-cube" data-bs-toggle="dropdown"
                    type="button">
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
      <item-price :address="address"></item-price>
      <!-- // End Price Card -->
      <!-- Start Owner & Collection -->
      <div class="row">
        <div class="col-xl-6 mx-auto mb-4">
          <item-owner :address="address"></item-owner>
        </div>
        <div class="col-xl-6 mb-4">
          <item-collection :address="address"></item-collection>
        </div>
      </div>
      <!-- // End Owner & Collection -->
      <!-- Start Details Card -->
      <item-details :address="address"></item-details>
      <!-- // End Details Card -->
    </template>
    <template #extra>
      <item-attributes :address="address"></item-attributes>
    </template>
  </item-layout>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import ItemLayout from "../layouts/ItemLayout.vue";
import ItemBreadcrumb from "../components/ItemBreadcrumb.vue";
import ItemContent from "../components/ItemContent.vue";
import ItemDescription from "../components/ItemDescription.vue";
import ItemPrice from "../components/ItemPrice.vue";
import ItemOwner from "../components/ItemOwner.vue";
import ItemCollection from "../components/ItemCollection.vue";
import ItemDetails from "../components/ItemDetails.vue";
import ItemAttributes from "../components/ItemAttributes.vue";

export default defineComponent({
  name: "ItemView",
  props: {
    address: {
      type: String,
      required: true,
    }
  },
  components: {
    ItemAttributes,
    ItemDetails,
    ItemCollection, ItemOwner, ItemPrice, ItemDescription, ItemContent, ItemBreadcrumb, ItemLayout
  },
})
</script>

<style lang="scss" scoped>

</style>
