package money.tegro.market.tool

import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import money.tegro.market.core.configuration.MarketplaceConfiguration
import money.tegro.market.core.dto.toSafeBounceable
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.*
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.cell.storeRef
import org.ton.crypto.Ed25519
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

    @Inject
    private lateinit var configuration: MarketplaceConfiguration

    @CommandLine.Option(names = ["--private-key"], description = ["Your wallet's private key (base64)"])
    private lateinit var privateKey: String

    @CommandLine.Option(names = ["--amount"], description = ["Amount that will be sent to initialize the contract"])
    private var amount = 100_000_000L

    override fun run() {
        runBlocking {
            val wallet = WalletV1R3(liteApi, PrivateKeyEd25519(base64(privateKey)))
            println("Your wallet address is ${wallet.address().toString(userFriendly = true)}")

            val stateInit = StateInit(
                code = BagOfCells(hex("B5EE9C7241020601000110000114FF00F4A413F4BCF2C80B01020120020301D4D23221C700915BE0D0D3030171B0915BE0FA4030ED44D0FA40D3FFD4D43005D31FD33F5365C7058E2F313334238101A4BA8E1001D3FFC85004CF16CBFFCC13CCC9ED5493303330E2810539BA98D4D43001FB04ED549130E2E035821005138D9112BAE3025F06840FF2F0040004F23001FE03FA4031D4D430D08308D718308200DEAD22F9004005F91013F2F47020C8CB0112F40012F400CB00C920F9007074C8CB02CA07CBFFC9D004D0FA00FA0030768018C8CB0527CF165003FA0212CB6B12CCC971FB00702082105FCC3D14C8CB1F14CB3F25CF165005CF1612CA0023FA0213CA00C9718018C8CB055003CF165003050012FA02CB6ACCC973FB000AE749AB")).roots.first(),
                data = CellBuilder.createCell {
                    storeTlb(MsgAddress.tlbCodec(), wallet.address()) // owner_address
                    storeBytes(Ed25519.publicKey(configuration.marketplaceAuthorizationPrivateKey)) // authorization public key
                    storeRef(BagOfCells(hex("B5EE9C7241020A010001B2000114FF00F4A413F4BCF2C80B01020120020302014804050004F2300202CD0607002FA03859DA89A1F481F481F481F401A861A1F401F481F4006101F7D00E8698180B8D8492F82707D201876A2687D207D207D207D006A1829A2E382C92F84F03813E380491BB9472203E98F90E000471A698390E0314708C100580019E98780C0BABCDD09F97A39016F00E031C709C100580019699398410C30B731B2B65D797A39C898714B1C03E99F9803F171106000C92F857010600140801F5D41081DCD650029285029185F7970E101E87D007D207D0018384008646582A804E78B28B9D090D0A85AD08A500AFD010AE5B564B8FD80384008646582AC678B2803FD010B65B564B8FD80384008646582A802E78B00FD0109E5B564B8FD80381041082FE61E8A10C00C646582A802E78B117D010A65B509E58F8A40900C49A3010471036552212F004E0395B06C0038E4882103B9ACA0015BEF2E1C95314C70559C705B1F2E1CA702082105FCC3D14218010C8CB055006CF1622FA0215CB6A14CB1F12CB3F23CF165003CF16CA0021FA02CA00C98100A0FB00E05F06840FF2F0002ACB3F22CF1658CF16CA0021FA02CA00C98100A0FB009FE63422")).roots.first())
                    storeRef { // amounts_cell
                        storeTlb(Coins.tlbCodec(), Coins.ofNano(50_000_000)); // deploy_amount
                        storeTlb(Coins.tlbCodec(), Coins.ofNano(50_000_000)); // transfer_amount
                    }
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
