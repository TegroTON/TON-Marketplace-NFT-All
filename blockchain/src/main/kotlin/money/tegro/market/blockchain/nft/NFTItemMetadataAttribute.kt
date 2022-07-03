package money.tegro.market.blockchain.nft

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class NFTItemMetadataAttribute(
    @JsonProperty("trait_type")
    val trait: String,
    val value: String
)
