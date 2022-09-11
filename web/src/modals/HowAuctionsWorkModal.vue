<template>
  <div class="modal fade" id="HowAuctionsWorkModal" tabindex="-1" aria-labelledby="HowAuctionsWorkModalLabel"
       aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered mobile-modal-bottom">
      <div class="modal-content border-0">
        <div class="modal-header border-0 mb-5">
          <h5 class="modal-title fs-24" id="SelectTypeModalLabel">How timed auctions work</h5>
          <button type="button" class="border-0 p-0 modal-close" data-bs-dismiss="modal" aria-label="Close"><i
              class="fa-solid fa-xmark fa-lg"></i></button>
        </div>
        <div class="modal-body">
          When you put your NFT on timed auction, you choose currency, minimum bid, starting and ending dates of your
          auction.<br><br>The bidder can only place a bid which satisfies the following conditions:<br><br>
          <ol class="sc-fKknU cScerJ">
            <li>It is at least minimum bid set for the auction</li>
            <li>It is at least 5% higher than the current highest bid in equivalent</li>
          </ol>
          <br>Note that some bids may disappear with time if the bidder withdraws their balance. At the same time, some
          bids may reappear if the bidder has topped up their balance again.<br><br>Auction cannot be cancelled after
          any valid bid was made. Any bid placed in the last 10 minutes extends the auction by 10 minutes.<br><br>In the
          48 hours after the auction ends you will only be able to accept the highest available bid placed during the
          auction. As with regular bids, you will need to pay some gas to accept it.Note that you can always decrease
          the price of your listing for free, without making a transaction or paying gas. You can view all your NFTs
          here.
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "HowAuctionsWorkModal"
}
</script>

<style scoped>

</style>
