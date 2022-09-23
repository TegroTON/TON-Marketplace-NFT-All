<template>
  <stats-bar>
    <stats-bar-entry>
      <template #name>Floor</template>
      <template #value>0 TON</template>
    </stats-bar-entry>
    <stats-bar-entry>
      <template #name>Volume</template>
      <template #value>1 TON</template>
    </stats-bar-entry>
    <stats-bar-entry>
      <template #name>Items</template>
      <template #value>1337</template>
    </stats-bar-entry>
    <stats-bar-entry>
      <template #name>Owners</template>
      <template #value>69</template>
    </stats-bar-entry>
    <stats-bar-entry>
      <template #name>Blockchain</template>
      <template #value>TON</template>
    </stats-bar-entry>
    <stats-bar-entry preserve-case>
      <template #name>Address</template>
      <template #value>djfasjdlkf</template>
    </stats-bar-entry>
  </stats-bar>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import StatsBar from "./StatsBar.vue";
import StatsBarEntry from "./StatsBarEntry.vue";

export default defineComponent({
  name: "CollectionStats",
  components: {StatsBarEntry, StatsBar},
  props: {
    address: {
      required: true,
      type: String,
    }
  },
})
</script>

<style lang="scss" scoped>

</style>
