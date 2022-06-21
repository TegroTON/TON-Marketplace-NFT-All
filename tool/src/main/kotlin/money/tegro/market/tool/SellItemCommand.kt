package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import money.tegro.market.blockchain.nft.NFTItem
import money.tegro.market.blockchain.nft.NFTSale
import money.tegro.market.blockchain.nft.NFTStubSidorovich
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.*
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.smartcontract.wallet.v1.WalletV1R3
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.constructor.tlbCodec
import org.ton.tlb.storeTlb
import kotlin.math.pow
import kotlin.system.exitProcess

class SellItemCommand :
    CliktCommand(name = "sell", help = "Puts an item to sell") {
    private val privateKey by option("--private-key", help = "Your wallet's private key (base64)").required()
    private val itemAddress by option("--item", help = "Item that is to be sold").required()

    private val marketplaceAddress by option(
        "--marketplace-address",
        help = "Marketplace address. Defaults to your wallet"
    )
    private val marketplaceFee by option("--marketplace-fee", help = "Marketplace fee (nanotons)").long()
    private val royaltyDestination by option("--royalty-destination", help = "Address that will receive royalty")
    private val royalty by option("--royalty", help = "Royalty (nanotons). If empty, then none").long()
    private val price by option("--price", help = "Price (nanotons) of the item").long().required()

    private val sendAmount by option(
        "--send-amount",
        help = "Amount that is sent to the NFT item contract, in nanotons"
    ).long()
        .default(200_000_000L)
    private val initAmount by option(
        "--init-amount",
        help = "Amount that is sent to the new NFT sale contract, in nanotons"
    ).long()
        .default(125_000_000L)

    private val queryId by option("--query-id", help = "Querry ID of the outbound message").long().default(0L)

    override fun run() {
        runBlocking {
            val wallet = WalletV1R3(Tool.currentLiteApi, PrivateKeyEd25519(base64(privateKey)))
            println("Your wallet address is ${wallet.address().toString(userFriendly = true)}")

            println("Querying item ${MsgAddressIntStd(itemAddress).toString(userFriendly = true)} information")
            val item = NFTItem.of(MsgAddressIntStd(itemAddress), Tool.currentLiteApi) ?: run {
                println("No such item, quitting")
                exitProcess(-1)
            }

            if (item.owner != wallet.address()) {
                println("Item owner address (${item.owner.toString(userFriendly = true)}) differs from provided address")
                println("Cannot proceed, quitting")
//                exitProcess(-1)
            }

            val stub = NFTStubSidorovich(
                marketplaceAddress?.let { MsgAddressIntStd(it) } ?: wallet.address(),
                item.address,
//                item.owner,
                wallet.address(),
                price,
                marketplaceFee ?: 0,
                royaltyDestination?.let { MsgAddressIntStd(it) },
                royalty,
            )
            println("Item sale contract address will be ${stub.address.toString(userFriendly = true)}")

            println(
                "Preparing a message to the item with a request to transfer item to ${stub.address.toString(userFriendly = true)}"
            )
            val message = wallet.createSigningMessage(wallet.seqno()) {
                storeUInt(3, 8) // send mode
                storeRef {
                    storeTlb(
                        MessageRelaxed.tlbCodec(AnyTlbConstructor), MessageRelaxed(
                            info = CommonMsgInfoRelaxed.IntMsgInfo(
                                ihrDisabled = true,
                                bounce = true,
                                bounced = false,
                                src = MsgAddressExtNone,
                                dest = item.address,
                                value = CurrencyCollection(
                                    coins = Coins.ofNano(sendAmount)
                                )
                            ),
                            init = null,
                            body =
                            CellBuilder.createCell {
                                storeUInt(0x5fcc3d14, 32) // OP, transfer
                                storeUInt(queryId, 64) // Query id
                                storeTlb(MsgAddress.tlbCodec(), stub.address) // new owner
                                storeTlb(MsgAddress.tlbCodec(), wallet.address()) // response destination
                                storeInt(0, 1)
                                storeTlb(Coins.tlbCodec(), Coins.ofNano(initAmount))
                                storeTlb(
                                    Either.tlbCodec(Cell.tlbCodec(), Cell.tlbCodec()),
                                    Either.of(Cell.of(), null)
                                )
                            },
                            storeBodyInRef = true,
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

            println("Sending the message")
            val result1 = Tool.currentLiteApi.sendMessage(
                Message(
                    CommonMsgInfo.ExtInMsgInfo(wallet.address()),
                    init = null,
                    body = body
                )
            )
            println("Result: $result1")

            runBlocking { delay(10000L) }

            println("Checking if item was correctly transferred")
            val itemUpdated = NFTItem.of(item.address, Tool.currentLiteApi)
            println("Tranfer ${item.owner.toString(userFriendly = true)} -> ${itemUpdated?.owner?.toString(userFriendly = true)}")
            if (itemUpdated != null && itemUpdated.owner == stub.address) {
                println("Huge success!")
            } else {
                println("Something went wrong")
            }

            println("Sending an external message to initialize sale contract")
            val result = Tool.currentLiteApi.sendMessage(
                Message(
                    CommonMsgInfo.ExtInMsgInfo(stub.address),
                    init = stub.stateInit(),
                    body = Cell.of()
                )
            )
            println("Result: $result")

            runBlocking { delay(10000L) }

            println("Checking sale contract initialization")
            NFTSale.of(stub.address, Tool.currentLiteApi)?.run {
                println("Success! Sale contract initialized:")
                println("\tOwner account ${address.toString(userFriendly = true)} implements an NFT-sale contract")
                println("\tMarketplace: ${marketplace.toString(userFriendly = true)}")
                println("\tSeller: ${owner.toString(userFriendly = true)}")
                println("\tPrice: ${price.toFloat() / 10f.pow(9f)} TON ($price nanoTON)")
                println("\tMarketplace fee:  ${marketplaceFee.toFloat() / 10f.pow(9f)} TON ($marketplaceFee nanoTON)")
                println("\tRoyalties:  ${this.royalty?.toFloat()?.div(10f.pow(9f))} TON ($royalty nanoTON)")
                println("\tRoyalty destination: ${royaltyDestination?.toString(userFriendly = true)}")
            } ?: run {
                println("Something went wrong. Check your account address, try increasing the init amount")
            }
        }
    }
}
