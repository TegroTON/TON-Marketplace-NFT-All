package money.tegro.market.tool

import money.tegro.market.core.dto.TransactionRequestDTO
import org.ton.block.*
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.lite.api.LiteApi
import org.ton.smartcontract.wallet.v1.WalletV1R3
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.storeTlb

suspend fun TransactionRequestDTO.performTransaction(wallet: WalletV1R3, liteApi: LiteApi) {
    val it = this
    val message = wallet.createSigningMessage(wallet.seqno()) {
        storeUInt(3, 8) // send mode
        storeRef {
            storeTlb(
                MessageRelaxed.tlbCodec(AnyTlbConstructor), MessageRelaxed(
                    info = CommonMsgInfoRelaxed.IntMsgInfoRelaxed(
                        ihrDisabled = true,
                        bounce = it.to.startsWith("E"), // Janky way to check if bounceable
                        bounced = false,
                        src = AddrNone,
                        dest = AddrStd(it.to),
                        value = CurrencyCollection(
                            coins = Coins.ofNano(it.value)
                        )
                    ),
                    init = null,
                    body = it.payload?.let { BagOfCells(base64(it)) }?.roots?.first() ?: Cell.of(),
                    storeBodyInRef = true,
                )
            )
        }
    }

    val signature = wallet.privateKey.sign(message.hash())

    println("Sending the message")
    liteApi.sendMessage(
        Message(
            ExtInMsgInfo(wallet.address()),
            init = null,
            body = CellBuilder.createCell {
                storeBytes(signature)
                storeBits(message.bits)
                storeRefs(message.refs)
            }
        )
    )
}
