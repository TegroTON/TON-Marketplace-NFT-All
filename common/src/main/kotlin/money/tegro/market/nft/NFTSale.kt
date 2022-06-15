package money.tegro.market.nft

import mu.KLogging
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressIntStd
import org.ton.block.VmStackValue
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.tlb.loadTlb

data class NFTSale(
    val address: MsgAddressIntStd,
    val marketplace: MsgAddressIntStd,
    val item: MsgAddressIntStd,
    val owner: MsgAddressIntStd,
    val price: Long,
    val marketplaceFee: Long,
    val royaltyDestination: MsgAddressIntStd?,
    val royalty: Long?,
) {
    companion object : KLogging() {
        private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

        @JvmStatic
        suspend fun of(
            address: MsgAddressIntStd,
            liteApi: LiteApi,
        ): NFTSale? {
            val referenceBlock = liteApi.getMasterchainInfo().last

            logger.debug("running method `get_sale_data` on ${address.toString(userFriendly = true)}")
            val result = liteApi.runSmcMethod(
                0b100, referenceBlock,
                LiteServerAccountId(address),
                "get_sale_data"
            )

            logger.debug("response: $result")
            if (result.exitCode != 0) {
                logger.debug { "contract doesn't have such method/method execution failed, assume not an NFTSale contract" }
                return null
            }

            return try {
                NFTSale(
                    address,
                    (result[0] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec) as MsgAddressIntStd,
                    (result[1] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec) as MsgAddressIntStd,
                    (result[2] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec) as MsgAddressIntStd,
                    (result[3] as VmStackValue.TinyInt).value,
                    (result[4] as VmStackValue.TinyInt).value,
                    (result[5] as VmStackValue.Slice).toCellSlice().loadTlb(msgAddressCodec) as? MsgAddressIntStd,
                    (result[6] as VmStackValue.TinyInt).value,
                )
            } catch (e: Exception) {
                logger.warn { "couldn't parse response: $e" }
                null
            }
        }
    }
}
