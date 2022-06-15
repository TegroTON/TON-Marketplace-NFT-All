package money.tegro.market.tool

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import money.tegro.market.nft.*
import money.tegro.market.ton.ResilientLiteClient
import mu.KLogging
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.*
import org.ton.block.tlb.tlbCodec
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.cell.exception.CellOverflowException
import org.ton.crypto.base64
import org.ton.crypto.hex
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.smartcontract.wallet.v1.WalletV1R3
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.storeTlb

data class ItemDump(
    val item: NFTItem,
    val royalty: NFTRoyalty?,
    val metadata: NFTMetadata?,
) {
    companion object {
        @JvmStatic
        suspend fun of(address: MsgAddressIntStd, liteClient: LiteApi): ItemDump? {
            val item = NFTItem.of(address, liteClient)
            return item?.let {
                ItemDump(
                    it,
                    NFTRoyalty.of(address, liteClient),
                    NFTMetadata.of(it.content(liteClient))
                )
            }
        }
    }
}

data class CollectionDump(
    val collection: NFTCollection,
    val royalty: NFTRoyalty?,
    val metadata: NFTMetadata?,
    val items: List<ItemDump>?,
)

interface LiteServerOptions {
    val host: Int
    val port: Int
    val publicKey: String
}

class MainnetLiteServerOptions : OptionGroup("lite server options [MAINNET]"), LiteServerOptions {
    override val host by option(
        "--lite-server-mainnet-host",
        help = "Lite server host IP address",
        envvar = "LITE_SERVER_MAINNET_HOST"
    )
        .int()
        .default(908566172)
    override val port by option(
        "--lite-server-mainnet-port",
        help = "Lite server port number",
        envvar = "LITE_SERVER_MAINNET_PORT"
    )
        .int()
        .default(51565)
    override val publicKey by option(
        "--lite-server-mainnet-public-key",
        help = "Lite server public key (base64)",
        envvar = "LITE_SERVER_MAINNET_PUBLIC_KEY"
    )
        .default("TDg+ILLlRugRB4Kpg3wXjPcoc+d+Eeb7kuVe16CS9z8=")
}

class SandboxLiteServerOptions : OptionGroup("lite server options [SANDBOX]"), LiteServerOptions {
    override val host by option(
        "--lite-server-sandbox-host",
        help = "Lite server host IP address",
        envvar = "LITE_SERVER_SANDBOX_HOST"
    )
        .int()
        .default(1426768764)
    override val port by option(
        "--lite-server-sandbox-port",
        help = "Lite server port number",
        envvar = "LITE_SERVER_SANDBOX_PORT"
    )
        .int()
        .default(13724)
    override val publicKey by option(
        "--lite-server-sandbox-public-key",
        help = "Lite server public key (base64)",
        envvar = "LITE_SERVER_SANDBOX_PUBLIC_KEY"
    )
        .default("R1KsqYlNks2Zows+I9s4ywhilbSevs9dH1x2KF9MeSU=")
}

class Tool :
    CliktCommand(name = "tool", help = "Your one-stop NFT item/collection shop") {
    private val liteServerMainnetOptions by MainnetLiteServerOptions()
    private val liteServerSandboxOptions by SandboxLiteServerOptions()

    val isMainnet by option(
        "--mainnet",
        help = "Work in mainnet instead of the safe sandbox. BEWARE"
    ).flag(default = false)

    override fun run() {
        runBlocking {
            liteClient = ResilientLiteClient(
                if (isMainnet) liteServerMainnetOptions.host else liteServerSandboxOptions.host,
                if (isMainnet) liteServerMainnetOptions.port else liteServerSandboxOptions.port,
                base64(
                    if (isMainnet) liteServerMainnetOptions.publicKey else liteServerSandboxOptions.publicKey
                )
            )

            logger.debug("connecting to the lite client")
            (liteClient as LiteClient).connect()
        }
    }

    companion object : KLogging() {
        lateinit var liteClient: LiteApi
    }
}

class QueryItem : CliktCommand(name = "query-item", help = "Query NFT item info") {
    private val dump by option("-d", "--dump", help = "Dump item information as json").flag()
    private val address by argument(name = "address", help = "NFT item contract address")

    override fun run() {
        runBlocking {
            val liteClient = Tool.liteClient


            if (dump) {
                jacksonObjectMapper().writeValueAsString(ItemDump.of(MsgAddressIntStd(address), liteClient))
                    .let { println(it) }
            } else {
                val item = NFTItem.of(MsgAddressIntStd(address), liteClient)
                val royalty = ((item as? NFTDeployedCollectionItem)?.collection ?: item?.address)?.let {
                    NFTRoyalty.of(it, liteClient)
                }
                val metadata = (item as? NFTDeployedItem)?.let {
                    NFTMetadata.of(it.content(liteClient))
                }
                println("NFT Item ${MsgAddressIntStd(address).toString(userFriendly = true)}:")
                println("\tInitialized: ${item != null}")
                item?.run {
                    println("\tIndex: ${index}")
                    println(
                        "\tCollection Address: ${
                            (this as? NFTDeployedCollectionItem)?.collection?.toString(
                                userFriendly = true
                            )
                        }"
                    )
                    println("\tOwner Address: ${owner.toString(userFriendly = true)}")

                    royalty?.run {
                        println("\tRoyalty percentage: ${value().times(100.0)}%")
                        println("\tRoyalty destination: ${destination.toString(userFriendly = true)}")
                    }

                    NFTSell.of(item.owner, liteClient)?.run {
                        println("\tOn sale: yes")
                        println("\tMarketplace: ${marketplace.toString(userFriendly = true)}")
                        println("\tSeller: ${owner.toString(userFriendly = true)}")
                        println("\tPrice: $price nTON")
                        println("\tMarketplace fee: $marketplaceFee nTON")
                        println("\tRoyalties: $royalty nTON")
                        println("\tRoyalty destination: ${royaltyDestination?.toString(userFriendly = true)}")
                    }

                    metadata?.run {
                        println("\tName: ${name}")
                        println("\tDescription: ${description}")
                        println("\tImage: ${image}")
                        println("\tImage data: ${imageData?.let { hex(it) }}")
                        println("\tAttributes:")
                        attributes.orEmpty().forEach {
                            println("\t\t${it.trait}: ${it.value}")
                        }
                    }
                }
            }
        }
    }
}

class QueryCollection :
    CliktCommand(name = "query-collection", help = "Query NFT collection info") {
    private val dump by option("-d", "--dump", help = "Dump collection information as json").flag()
    private val dumpItems by option(
        "--dump-items",
        help = "Dump information about each item as well. May take a while"
    ).flag()
    private val address by argument(name = "address", help = "NFT collection contract address")

    override fun run() {
        runBlocking {
            val liteClient = Tool.liteClient

            val collection = NFTCollection.of(MsgAddressIntStd.parse(address), liteClient) as NFTDeployedCollection
            val royalty = NFTRoyalty.of(collection.address, liteClient)
            val metadata = NFTMetadata.of(collection.content)

            if (dump) {
                jacksonObjectMapper().writeValueAsString(
                    CollectionDump(
                        collection,
                        royalty,
                        metadata,
                        if (dumpItems) collection.itemAddresses(liteClient).map { ItemDump.of(it, liteClient) }
                            .filterNotNull()
                            .toList() else null
                    )
                )
                    .let { println(it) }
            } else {
                println("NFT Collection ${collection.address.toString(userFriendly = true)}")
                println("\tNumber of items: ${collection.nextItemIndex}")
                println("\tOwner address: ${collection.owner.toString(userFriendly = true)}")

                royalty?.run {
                    println("\tRoyalty percentage: ${value().times(100.0)}%")
                    println("\tRoyalty destination: ${destination.toString(userFriendly = true)}")
                }

                metadata.run {
                    println("\tName: ${this.name}")
                    println("\tDescription: ${this.description}")
                    println("\tImage: ${this.image}")
                    println("\tImage data: ${this.imageData?.let { hex(it) }}")
                    println("\tCover image: ${this.coverImage}")
                    println("\tCover image data: ${this.coverImageData?.let { hex(it) }}")
                }
            }
        }
    }
}

class ListCollection :
    CliktCommand(name = "list-collection", help = "List all items of the given NFT collection") {
    private val address by argument(name = "address", help = "NFT collection contract address")

    override fun run() {
        runBlocking {
            val liteClient = Tool.liteClient

            val collection = NFTCollection.of(MsgAddressIntStd.parse(address), liteClient) as NFTDeployedCollection

            println("index | address | owner")

            collection.items(liteClient)
                .filterNotNull()
                .collect {
                    println(
                        "${it.index} | ${it.address.toString(userFriendly = true)} | ${
                            it.owner.toString(
                                userFriendly = true
                            )
                        }"
                    )
                }
        }
    }
}

class MintItem : CliktCommand(name = "mint-item", help = "Mint a standalone item") {
    private val messageCodec by lazy { Message.tlbCodec(AnyTlbConstructor) }
    private val privateKeyBase64 by option(
        "--private-key",
        help = "Your wallet's private key encoded as base64"
    ).required()

    override fun run() {
        runBlocking {
            val liteClient = Tool.liteClient

            val privateKey = PrivateKeyEd25519(base64(privateKeyBase64))
            val wallet = WalletV1R3(liteClient, privateKey)
            logger.debug("wallet public key: ${hex(wallet.getPublicKey().toString())}")
            val address = wallet.address()

            logger.debug("wallet address ${address.toString(bounceable = true)}")

            val lastBlock = liteClient.getMasterchainInfo().last

            val stub = NFTStubStandaloneItem(
                address,
                CellBuilder.createCell {
                    storeBytes(byteArrayOf(0x01.toByte()) + "https://cloudflare-ipfs.com/ipfs/bafybeiayqk3ml3gijcvfzl3qrya6p43darej4zpjln4ptf4v66ffzbnyee/16.json".toByteArray())
                },
            )
            logger.debug("New NFT item address: ${stub.address.toString(userFriendly = true)}")

            val message = wallet.createSigningMessage(wallet.seqno()) {
                storeUInt(3, 8) // send mode
                storeRef {
                    storeTlb(
                        MessageRelaxed.tlbCodec(AnyTlbConstructor), MessageRelaxed(
                            info = CommonMsgInfoRelaxed.IntMsgInfo(
                                ihrDisabled = true,
                                bounce = false,
                                bounced = false,
                                src = MsgAddressExtNone,
                                dest = stub.address,
                                value = CurrencyCollection(
                                    coins = Coins.ofNano(1_000_000L)
                                )
                            ),
                            init = stub.stateInit(),
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
            logger.debug("sending the message")
            val result = liteClient.sendMessage(
                Message(
                    CommonMsgInfo.ExtInMsgInfo(wallet.address()),
                    init = null,
                    body = body
                )
            )
            logger.info { result.toString() }
        }
    }

    companion object : KLogging()
}

fun main(args: Array<String>) =
    Tool().subcommands(QueryItem(), QueryCollection(), ListCollection(), MintItem()).main(args)
