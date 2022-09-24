<template>
  <div class="row">
    <!-- Start Item -->
    <div v-for="item in items" class="col-sm-6 col-xl-4 col-xxl-3 mb-4">
      <item-card :address="item"></item-card>
    </div>
  </div>
  <button class="btn btn-outline-secondary mt-3 w-100" type="button" @click="loadMore()">LOAD MORE</button>
</template>

<script lang="ts">
import {defineComponent, ref} from "vue";
import {useCollectionItemsQuery} from "../graphql/generated";
import ItemCard from "./ItemCard.vue";

export default defineComponent({
  name: "CollectionItemsList",
  components: {ItemCard},
  props: {
    address: {
      type: String,
      required: true,
    },
  },
  setup(props) {
    const drop = ref(0)
    const take = ref(16)

    const response = useCollectionItemsQuery({variables: {address: props.address, drop: drop, take: take}})

    return {
      drop: drop,
      take: take,
      data: response.data
    }
  },
  computed: {
    items() {
      return this.data?.collection?.items?.map(value => value.address) ?? []
    }
  },
  methods: {
    loadMore() {
      this.take = this.take + 8
    }
  }
})
</script>

<style lang="scss" scoped>

</style>
