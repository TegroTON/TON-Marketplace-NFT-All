package money.tegro.market.metadata

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class ItemMetadataAttribute(
    @JsonProperty("trait_type")
    val trait: String,
    val value: String
)
