package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import money.tegro.market.service.item.ItemContractService
import money.tegro.market.service.item.ItemMetadataService
import money.tegro.market.service.item.ItemOwnerService
import money.tegro.market.service.item.ItemSaleService
import money.tegro.market.toRaw
import org.springframework.beans.factory.annotation.Autowired
import org.ton.bigint.BigInt
import org.ton.block.MsgAddressInt

@GraphQLName("Item")
data class ItemQuery(
    @GraphQLIgnore
    val address: MsgAddressInt,
) {
    @GraphQLName("address")
    val addressString: String = address.toRaw()

    suspend fun index(
        @GraphQLIgnore @Autowired itemContractService: ItemContractService,
    ) =
        itemContractService.get(address)?.index?.toString()

    suspend fun collection(
        @GraphQLIgnore @Autowired itemContractService: ItemContractService,
    ) =
        (itemContractService.get(address)?.collection as? MsgAddressInt)?.let { CollectionQuery(it) }

    suspend fun owner(
        @GraphQLIgnore @Autowired itemOwnerService: ItemOwnerService,
    ) =
        itemOwnerService.get(address)?.toRaw()

    suspend fun name(
        @GraphQLIgnore @Autowired itemMetadataService: ItemMetadataService,
    ) =
        itemMetadataService.get(address)?.name

    suspend fun description(
        @GraphQLIgnore @Autowired itemMetadataService: ItemMetadataService,
    ) =
        itemMetadataService.get(address)?.description

    suspend fun image(
        @GraphQLIgnore @Autowired itemMetadataService: ItemMetadataService,
    ) =
        itemMetadataService.get(address)?.image

    suspend fun attributes(
        @GraphQLIgnore @Autowired itemMetadataService: ItemMetadataService,
    ) =
        itemMetadataService.get(address)?.attributes.orEmpty()
            .map { ItemAttributeQuery(it) }

    suspend fun isOnSale(
        @GraphQLIgnore @Autowired itemSaleService: ItemSaleService,
    ) =
        itemSaleService.get(address) != null

    suspend fun saleAddress(
        @GraphQLIgnore @Autowired itemOwnerService: ItemOwnerService,
        @GraphQLIgnore @Autowired itemSaleService: ItemSaleService,
    ) =
        if (itemSaleService.get(address) != null) {
            itemOwnerService.get(address)?.toRaw()
        } else {
            null
        }

    suspend fun fullPrice(
        @GraphQLIgnore @Autowired itemSaleService: ItemSaleService,
    ) =
        itemSaleService.get(address)?.full_price?.toString() ?: "0"

    suspend fun royaltyAmount(
        @GraphQLIgnore @Autowired itemSaleService: ItemSaleService,
    ) =
        itemSaleService.get(address)?.royalty?.toString() ?: "0"

    suspend fun marketplaceFee(
        @GraphQLIgnore @Autowired itemSaleService: ItemSaleService,
    ) =
        itemSaleService.get(address)?.marketplace_fee?.toString() ?: "0"

    fun networkFee() = SALE_NETWORK_FEE.toString() // 1 TON

    suspend fun buyPrice(
        @GraphQLIgnore @Autowired itemSaleService: ItemSaleService,
    ) =
        itemSaleService.get(address)?.full_price?.plus(SALE_NETWORK_FEE)?.toString() ?: "0"

    companion object {
        private val SALE_NETWORK_FEE = BigInt(1_000_000_000L)
    }
}
