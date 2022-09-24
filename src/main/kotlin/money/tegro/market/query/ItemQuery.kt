package money.tegro.market.query

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.scalars.ID
import money.tegro.market.contract.op.item.ItemOp
import money.tegro.market.contract.op.item.TransferOp
import money.tegro.market.metadata.ItemMetadataAttribute
import money.tegro.market.service.item.*
import money.tegro.market.toRaw
import org.springframework.beans.factory.annotation.Autowired
import org.ton.bigint.BigInt
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.SecureRandom
import org.ton.tlb.storeTlb
import kotlin.random.nextULong

@GraphQLName("Item")
data class ItemQuery(
    @GraphQLIgnore
    val address: MsgAddressInt,
) {
    @GraphQLName("address")
    val addressString: ID = ID(address.toRaw())

    suspend fun index(
        @GraphQLIgnore @Autowired itemContractService: ItemContractService,
    ) =
        itemContractService.get(address)?.index?.toString()

    suspend fun collection(
        @GraphQLIgnore @Autowired itemContractService: ItemContractService,
    ) =
        (itemContractService.get(address)?.collection as? MsgAddressInt)?.let { CollectionQuery(it) }

    suspend fun owner(
        @GraphQLIgnore @Autowired itemOwnerAddressService: ItemOwnerAddressService,
    ) =
        (itemOwnerAddressService.get(address) as? MsgAddressInt)?.let { ProfileQuery(it) }

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

    @GraphQLName("ItemAttribute")
    data class AttributeQuery(
        val trait: String,
        val value: String,
    ) {
        constructor(it: ItemMetadataAttribute) : this(
            it.trait,
            it.value
        )
    }

    suspend fun attributes(
        @GraphQLIgnore @Autowired itemMetadataService: ItemMetadataService,
    ) =
        itemMetadataService.get(address)?.attributes.orEmpty()
            .map { AttributeQuery(it) }

    suspend fun sale(
        @GraphQLIgnore @Autowired itemSaleAddressService: ItemSaleAddressService,
    ) =
        itemSaleAddressService.get(address)?.let { SaleQuery(it) }

    suspend fun royalty(
        @GraphQLIgnore @Autowired itemRoyaltyService: ItemRoyaltyService,
    ) =
        itemRoyaltyService.get(address)?.let { RoyaltyQuery(it) }

    suspend fun transfer(
        newOwner: String,
        responseDestination: String?,
    ) = TransactionRequestQuery(
        dest = address,
        value = BigInt(100_000_000),
        stateInit = null,
        text = "NFT Item Transfer",
        payload = CellBuilder.createCell {
            storeTlb(
                ItemOp, TransferOp(
                    query_id = SecureRandom.nextULong(),
                    new_owner = MsgAddressInt(newOwner),
                    response_destination = responseDestination?.let { MsgAddressInt(it) } ?: AddrNone,
                    custom_payload = Maybe.of(null),
                    forward_amount = VarUInteger(BigInt(50_000_000)),
                    forward_payload = Either.of(Cell.of(), null)
                )
            )
        }
    )
}
