<template>
  <stats-bar>
    <stats-bar-entry v-if="false">
      <template #name>Floor</template>
      <template #value>0 TON</template>
    </stats-bar-entry>
    <stats-bar-entry v-if="false">
      <template #name>Volume</template>
      <template #value>1 TON</template>
    </stats-bar-entry>
    <stats-bar-entry>
      <template #name>Items</template>
      <template #value>{{ itemNumber }}</template>
    </stats-bar-entry>
    <stats-bar-entry>
      <template #name>Owners</template>
      <template #value>{{ ownerNumber }}</template>
    </stats-bar-entry>
    <stats-bar-entry>
      <template #name>Blockchain</template>
      <template #value>TON</template>
    </stats-bar-entry>
    <stats-bar-entry preserve-case>
      <template #name>Address</template>
      <template #value>{{ formattedAddress }}</template>
    </stats-bar-entry>
  </stats-bar>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import StatsBar from "./StatsBar.vue";
import StatsBarEntry from "./StatsBarEntry.vue";
import {useCollectionStatsQuery} from "../graphql/generated";
import {normalizeAndShorten} from "../utility";

export default defineComponent({
  name: "CollectionStats",
  components: {StatsBarEntry, StatsBar},
  props: {
    address: {
      required: true,
      type: String,
    }
  },
  setup(props) {
    const response = useCollectionStatsQuery({variables: {address: props.address}})

    return {
      data: response.data
    }
  },
  computed: {
    itemNumber() {
      return this.data?.collection?.itemNumber ?? '...'
    },
    ownerNumber() {
      return this.data?.collection?.ownerNumber ?? '...'
    },
    formattedAddress() {
      return normalizeAndShorten(this.address)
    },
  }
})
</script>
