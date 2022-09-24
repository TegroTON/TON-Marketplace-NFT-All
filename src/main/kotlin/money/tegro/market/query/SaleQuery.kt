package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import money.tegro.market.service.SaleService
import money.tegro.market.toRaw
import org.springframework.beans.factory.annotation.Autowired
import org.ton.bigint.BigInt
import org.ton.block.MsgAddressInt

@GraphQLName("Sale")
data class SaleQuery(
    @GraphQLIgnore
    val address: MsgAddressInt
) {
    @GraphQLName("address")
    val addressString: ID = ID(address.toRaw())

    suspend fun fullPrice(
        @GraphQLIgnore @Autowired saleService: SaleService,
    ) =
        saleService.get(address)?.full_price?.toString()

    suspend fun royaltyAmount(
        @GraphQLIgnore @Autowired saleService: SaleService,
    ) =
        saleService.get(address)?.royalty?.toString()

    suspend fun marketplaceFee(
        @GraphQLIgnore @Autowired saleService: SaleService,
    ) =
        saleService.get(address)?.marketplace_fee?.toString()

    fun networkFee() = SALE_NETWORK_FEE.toString() // 1 TON

    suspend fun buyPrice(
        @GraphQLIgnore @Autowired saleService: SaleService,
    ) =
        saleService.get(address)?.full_price?.plus(SALE_NETWORK_FEE)?.toString()

    companion object {
        private val SALE_NETWORK_FEE = BigInt(1_000_000_000L)
    }
}
