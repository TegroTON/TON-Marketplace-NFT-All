<template>
  <nav aria-label="breadcrumb">
    <ol class="breadcrumb mb-5 ms-3">
      <li class="breadcrumb-item"><a href="/explore.php">Explore</a></li>
      <li class="breadcrumb-item"><a href="/collection.php">Tegro Cat</a></li>
      <li aria-current="page" class="breadcrumb-item active">Tegro Cat #133</li>
    </ol>
  </nav>
</template>

<script lang="ts">
import {defineComponent} from "vue";

export default defineComponent({
  name: "ItemBreadcrumb",
})
</script>

<style lang="scss" scoped>

</style>
