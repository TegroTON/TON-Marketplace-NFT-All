package money.tegro.market.tool

import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.client.ResilientLiteClient
import money.tegro.market.core.dto.toSafeBounceable
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.*
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.crypto.hex
import org.ton.lite.api.LiteApi
import org.ton.smartcontract.wallet.v1.WalletV1R3
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.storeTlb
import picocli.CommandLine


@CommandLine.Command(name = "deploy-market", description = ["Deploy the main market contract"])
class DeployMarketCommand : Runnable {
    @Inject
    private lateinit var liteApi: LiteApi

    @CommandLine.Option(names = ["--private-key"], description = ["Your wallet's private key (base64)"])
    private lateinit var privateKey: String

    @CommandLine.Option(names = ["--amount"], description = ["Amount that will be sent to initialize the contract"])
    private var amount = 100_000_000L

    override fun run() {
        runBlocking {
            (liteApi as ResilientLiteClient).connect()
            val wallet = WalletV1R3(liteApi, PrivateKeyEd25519(base64(privateKey)))
            println("Your wallet address is ${wallet.address().toString(userFriendly = true)}")

            val stateInit = StateInit(
                code = BagOfCells(hex("B5EE9C724101050100BA000114FF00F4A413F4BCF2C80B0102012002030164D23221C700915BE0D0D3030171B0915BE0FA4030ED44D0FA40D4303102D31FD33F30821005138D9112BAE3025F03840FF2F0040004F23000DAC8C97020C8CB0115F400F40013CB00C920F9007074C8CB02CA07CBFFC9D0C8C9778018C8CB055003CF16820AFAF080FA0212CB6B12CCCCC971FB00702082105FCC3D14218018C8CB0525CF16820AFAF080FA02CB6ACB1F14CB3F58CF16F828CF1612CA0021FA02CA00C972FB007B2FA113")).roots.first(),
                data = CellBuilder.createCell {
                    storeTlb(MsgAddress.tlbCodec(), wallet.address()) // owner_address
                    storeRef(BagOfCells(hex("B5EE9C7241020B0100019D000114FF00F4A413F4BCF2C80B01020120020302014804050004F2300202CD0607002FA03859DA89A1F481F481F481F401A861A1F401F481F4006102F7D00E8698180B8D8492F82707D201876A2687D207D207D207D006A18126BA4E10159C70D1A1A9A9A8A2198642802E78B2801E78B00E78B00FD016664F6AA7013638048B84A83698FA83BF13810E00049989BB94B1803E99F9803F1106000C92F85701060014D180823881B2A91097802F01CAD836001F1812F834207C080901F5660840EE6B280149428148C2FBCB87080F43E803E903E800C1C20043232C1540273C5945CE84868542D684528057E808572DAB25C7EC01C20043232C15633C59401FE8085B2DAB25C7EC01C20043232C1540173C5807E8084F2DAB25C7EC01C08208417F30F450860063232C1540173C588BE808532DA84F2C7C520A009082103B9ACA0015BEF2E1C95314C70559C705B1F2E1CA702082105FCC3D14218010C8CB055006CF1622FA0215CB6A14CB1F12CB3F23CF165003CF16CA0021FA02CA00C98100A0FB000004F2F0002ACB3F22CF1658CF16CA0021FA02CA00C98100A0FB002DD4156A")).roots.first())
                }
            )

            val address =
                AddrStd(0, CellBuilder.createCell { storeTlb(StateInit.tlbCodec(), stateInit) }.hash())
            println("Its address will be ${address.toSafeBounceable()}")

            val message = wallet.createSigningMessage(wallet.seqno()) {
                storeUInt(3, 8) // send mode
                storeRef {
                    storeTlb(
                        MessageRelaxed.tlbCodec(AnyTlbConstructor), MessageRelaxed(
                            info = CommonMsgInfoRelaxed.IntMsgInfoRelaxed(
                                ihrDisabled = true,
                                bounce = false,
                                bounced = false,
                                src = AddrNone,
                                dest = address,
                                value = CurrencyCollection(
                                    coins = Coins.ofNano(amount)
                                )
                            ),
                            init = stateInit,
                            body = Cell.of(),
                            storeBodyInRef = false
                        )
                    )
                }
            }

            val signature = wallet.privateKey.sign(message.hash())
            val body = CellBuilder.createCell {
                storeBytes(signature)
                storeBits(message.bits)
                storeRefs(message.refs)
            }

            println("Sending the initialization message")
            val result = liteApi.sendMessage(
                Message(
                    ExtInMsgInfo(wallet.address()),
                    init = null,
                    body = body
                )
            )
            println("Result: $result")
        }
    }
}
