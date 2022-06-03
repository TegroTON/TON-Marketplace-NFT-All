package money.tegro.market.tool

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import io.ipfs.kotlin.IPFS
import io.ipfs.kotlin.IPFSConfiguration
import kotlinx.coroutines.runBlocking
import money.tegro.market.nft.*
import mu.KLogging
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import org.kodein.di.conf.ConfigurableDI
import org.kodein.di.instance
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.block.*
import org.ton.block.tlb.tlbCodec
import org.ton.cell.CellBuilder
import org.ton.crypto.base64
import org.ton.crypto.hex
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerAccountId
import org.ton.lite.client.LiteClient
import org.ton.smartcontract.wallet.SimpleWalletR3
import org.ton.tlb.constructor.AnyTlbConstructor

class LiteServerOptions : OptionGroup("lite server options") {
    val host by option("--lite-server-host", help = "Lite server host IP address", envvar = "LITE_SERVER_HOST")
        .int()
        .default(1426768764)
    val port by option("--lite-server-port", help = "Lite server port number", envvar = "LITE_SERVER_PORT")
        .int()
        .default(13724)
    val publicKey by option(
        "--lite-server-public-key",
        help = "Lite server public key (base64)",
        envvar = "LITE_SERVER_PUBLIC_KEY"
    )
        .default("R1KsqYlNks2Zows+I9s4ywhilbSevs9dH1x2KF9MeSU=")
}

class IPFSOptions : OptionGroup("IPFS options") {
    val url by option("--ipfs-url", help = "IPFS API server url", envvar = "IPFS_URL")
        .default("http://127.0.0.1:5001/api/v0/")
}

class Tool(override val di: ConfigurableDI) :
    CliktCommand(name = "tool", help = "Your one-stop NFT item/collection shop"),
    DIAware {
    private val liteServerOptions by LiteServerOptions()
    private val ipfsOptions by IPFSOptions()

    override fun run() {
        runBlocking {
            di.addConfig {
                bindSingleton<LiteApi> {
                    LiteClient(
                        liteServerOptions.host,
                        liteServerOptions.port,
                        liteServerOptions.publicKey.let { base64(it) })
                }
                bindSingleton { IPFS(IPFSConfiguration(ipfsOptions.url)) }
            }

            val liteClient: LiteApi by instance()

            logger.debug("connecting to the lite client at ${liteServerOptions.host}:${liteServerOptions.port}")
            (liteClient as LiteClient).connect()

            val ipfs: IPFS by instance()

            logger.debug("IPFS API is initialized")
        }
    }

    companion object : KLogging()
}

class QueryItem(override val di: DI) : CliktCommand(name = "query-item", help = "Query NFT item info"), DIAware {
    val address by argument(name = "address", help = "NFT item contract address")
    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()

            val item = liteClient.getNFTItem(MsgAddressInt.AddrStd.parse(address))
            println("NFT Item ${item.address.toString(userFriendly = true)}:")
            println("\tInitialized: ${item is NFTItemInitialized}")
            if (item is NFTItemInitialized) {
                println("\tIndex: ${item.index}")
                println("\tCollection Address: ${item.collection?.toString(userFriendly = true)}")
                println("\tOwner Address: ${item.owner.toString(userFriendly = true)}")

                val fullContent = item.collection?.let {
                    liteClient.getNFTCollectionItemContent(
                        it, item.index, item.content
                    )
                } ?: item.content

                val content = fullContent.let {
                    if (isOnChainContent(it)) {
                        TODO()
                    } else {
                        val ipfs: IPFS by instance()
                        NFTContentOffChain.parse(ipfs, it)
                    }
                }
                println("\tName: ${content.name}")
                println("\tDescription: ${content.description}")
                println("\tImage: ${content.image}")

                (item.collection?.let { liteClient.getNFTCollectionRoyalties(it) }
                    ?: liteClient.getNFTItemRoyalties(item.address))
                    ?.let { royalties ->
                        println("\tRoyalty percentage: ${royalties.first.toFloat() * 100.0 / royalties.second}%")
                        println("\tRoyalty destination: ${royalties.third.toString(userFriendly = true)}")
                    }
            }
        }

    }
}

class QueryCollection(override val di: DI) :
    CliktCommand(name = "query-collection", help = "Query NFT collection info"), DIAware {
    val address by argument(name = "address", help = "NFT collection contract address")

    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()

            val collection = liteClient.getNFTCollection(MsgAddressInt.AddrStd.parse(address))
            println("NFT Collection ${collection.address.toString(userFriendly = true)}")
            println("\tNumber of items: ${collection.size}")
            println("\tOwner address: ${collection.owner.toString(userFriendly = true)}")

            val content = if (isOnChainContent(collection.content)) {
                TODO()
            } else {
                val ipfs: IPFS by instance()
                NFTContentOffChain.parse(ipfs, collection.content)
            }
            println("\tName: ${content.name}")
            println("\tDescription: ${content.description}")
            println("\tImage: ${content.image}")

            liteClient.getNFTCollectionRoyalties(collection.address)?.let {
                println("\tRoyalty percentage: ${it.first.toFloat() * 100.0 / it.second}%")
                println("\tRoyalty destination: ${it.third.toString(userFriendly = true)}")
            }
        }
    }
}

class ListCollection(override val di: DI) :
    CliktCommand(name = "list-collection", help = "List all items of the given NFT collection"),
    DIAware {
    val address by argument(name = "address", help = "NFT collection contract address")

    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()

            val collection = liteClient.getNFTCollection(MsgAddressInt.AddrStd.parse(address))

            println("index | address | owner")

            for (i in 0 until collection.size) {
                val item = liteClient.getNFTCollectionItem(collection, i)
                if (item is NFTItemInitialized)
                    println(
                        "${item.index} | ${item.address.toString(userFriendly = true)} | ${
                            item.owner.toString(
                                userFriendly = true
                            )
                        }"
                    )
            }
        }
    }
}

class MintItem(override val di: DI) : CliktCommand(name = "mint-item", help = "Mint a standalone item"), DIAware {
    private val messageCodec by lazy { Message.tlbCodec(AnyTlbConstructor) }
    private val privateKeyBase64 by option(
        "--private-key",
        help = "Your wallet's private key encoded as base64"
    ).required()

    override fun run() {
        runBlocking {
            val liteClient: LiteApi by instance()

            val privateKey = PrivateKeyEd25519(base64(privateKeyBase64))
            val wallet = SimpleWalletR3(liteClient, privateKey, 0)
            logger.debug("wallet public key: ${hex(wallet.publicKey.key)}")
            val address = wallet.address()

            logger.debug("wallet address ${address.toString(bounceable = true)}")

            val lastBlock = liteClient.getMasterchainInfo().last

            val stub = NFTItemStub(
                CellBuilder.createCell {
                    storeBytes(byteArrayOf(0x01.toByte()) + "https://youtu.be/dQw4w9WgXcQ".toByteArray())
                },
                address
            )
            logger.debug("stub NFT address: ${stub.address.toString(userFriendly = true)}")

            logger.debug("trying to get wallet's seqno")
            val seqnoResult = liteClient.runSmcMethod(0b100, lastBlock, LiteServerAccountId(address), "seqno")

            logger.debug("seqno: ${(seqnoResult.first() as VmStackValue.TinyInt).value}")
            val message =
                CellBuilder.createCell {
                    storeUInt((seqnoResult.first() as VmStackValue.TinyInt).value, 32)
                    storeUInt(3, 8)
                    storeRef(stub.initMessage())
                }

            val signature = wallet.privateKey.sign(message.hash())
            val body = CellBuilder.createCell {
                storeBytes(signature)
                storeBits(message.bits)
                storeRefs(message.refs)
            }

            logger.debug("sending the message")
//            val result = liteClient.sendMessage(
//                Message(
//                    CommonMsgInfo.ExtInMsgInfo(wallet.address()),
//                    init = null,
//                    body = body
//                )
//            )
            wallet.transfer(
                stub.address,
                (seqnoResult.first() as VmStackValue.TinyInt).value.toInt(),
                Coins(VarUInteger(100_000_000L)),
                null,
                bounce = false
            )
//            logger.info { result.toString() }
        }
    }

    companion object : KLogging()
}

fun main(args: Array<String>) {
    val di = ConfigurableDI()
    Tool(di).subcommands(QueryItem(di), QueryCollection(di), ListCollection(di), MintItem(di)).main(args)
}
