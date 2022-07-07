package money.tegro.market.nightcrawler

import io.micronaut.context.annotation.Prototype
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import money.tegro.market.blockchain.nft.*
import money.tegro.market.core.model.*
import money.tegro.market.core.repository.*
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.value
import org.ton.api.tonnode.TonNodeBlockIdExt
import org.ton.block.AddrNone
import org.ton.block.AddrStd
import org.ton.lite.api.LiteApi
import reactor.core.Exceptions
import reactor.core.publisher.Sinks
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.extra.bool.not
import java.time.Duration
import java.time.Instant

/**
 * This bitch will trigger events in an avalanche-like fashion
 * Once this fucktrain gets moving, there's no stopping - try completing all the sinks
 */
@Prototype
class Workers(
    private val configuration: NightcrawlerConfiguration,
    private val liteApi: LiteApi,
    private val accountRepository: AccountRepository,
    private val collectionRepository: CollectionRepository,
    private val itemRepository: ItemRepository,
    private val royaltyRepository: RoyaltyRepository,
    private val saleRepository: SaleRepository,
) : Runnable {
    lateinit var referenceBlock: suspend () -> TonNodeBlockIdExt
    val accounts: Sinks.Many<AddrStd> = Sinks.many().replay().latest()
    val collections: Sinks.Many<AddrStd> = Sinks.many().replay().latest()
    val items: Sinks.Many<AddrStd> = Sinks.many().replay().latest()
    val royalties: Sinks.Many<AddrStd> = Sinks.many().replay().latest()
    val sales: Sinks.Many<AddrStd> = Sinks.many().replay().latest()

    override fun run() {
        accounts.asFlux()
            .concatMap(::processAccount)
            .subscribe()

        collections.asFlux()
            .concatMap(::processCollection)
            .concatMap(::processMissingItems)
            .subscribe()

        items.asFlux()
            .concatMap(::processItem)
            .subscribe()

        royalties.asFlux()
            .concatMap(::processRoyalty)
            .subscribe()

        sales.asFlux()
            .concatMap(::processSale)
            .subscribe()
    }

    fun processAccount(address: AddrStd) = mono {
        accountRepository.findById(address).awaitSingleOrNull()?.let { dbAccount ->
            if (Duration.between(dbAccount.updated, Instant.now()) > configuration.accountUpdatePeriod) {
                logger.debug("updating existing account {} blockchain data", value("address", dbAccount.address))

                val new = dbAccount.copy(updated = Instant.now())

                accountRepository.update(new).awaitSingleOrNull()
            } else {
                logger.debug(
                    "account {} blockchain data is up-to-date, last updated {}",
                    value("address", dbAccount.address),
                    value("updated", dbAccount.updated)
                )
                dbAccount
            }
        } ?: run {
            logger.debug("saving new account {}", value("address", address))

            val new = AccountModel(
                address = address
            )

            accountRepository.save(new).awaitSingleOrNull()
        }
    }

    fun processCollection(address: AddrStd) = mono {
        collectionRepository.findById(address).awaitSingleOrNull()?.let { dbCollection ->
            if (Duration.between(dbCollection.updated, Instant.now()) > configuration.collectionUpdatePeriod) {
                logger.debug("updating existing collection {} blockchain data", value("address", dbCollection.address))

                val collection = NFTCollection.of(dbCollection.address, liteApi, referenceBlock)

                var new = dbCollection.copy(
                    nextItemIndex = collection.nextItemIndex,
                    owner = collection.owner,
                    updated = Instant.now(),
                )

                if (Duration.between(
                        dbCollection.metadataUpdated,
                        Instant.now()
                    ) > configuration.collectionMetadataUpdatePeriod
                ) {
                    logger.debug("updating existing collection {} metadata", value("address", dbCollection.address))
                    val metadata = collection.metadata()

                    new = new.copy(
                        name = metadata.name.orEmpty(),
                        description = metadata.description.orEmpty(),
                        image = metadata.image,
                        imageData = metadata.imageData ?: byteArrayOf(),
                        coverImage = metadata.coverImage,
                        coverImageData = metadata.coverImageData ?: byteArrayOf(),
                        metadataUpdated = Instant.now(),
                    )
                } else {
                    logger.debug(
                        "collection {} metadata is up-to-date, last updated {}",
                        value("address", dbCollection.address),
                        value("updated", dbCollection.metadataUpdated)
                    )
                }

                // Trigger other jobs
                (dbCollection.owner as? AddrStd)?.let { accounts.tryEmitNext(it) }
                if (dbCollection.owner != new.owner) // In case owner was changed, update both
                    (new.owner as? AddrStd)?.let { accounts.tryEmitNext(it) }

                royalties.tryEmitNext(new.address)

                collectionRepository.update(new).awaitSingleOrNull()
            } else {
                logger.debug(
                    "collection {} blockchain data is up-to-date, last updated {}",
                    value("address", dbCollection.address),
                    value("updated", dbCollection.updated)
                )
                dbCollection
            }
        } ?: run {
            logger.debug("saving new collection {}", value("address", address))

            val collection = NFTCollection.of(address, liteApi, referenceBlock)
            val metadata = collection.metadata()

            val new = CollectionModel(
                address = collection.address as AddrStd,
                nextItemIndex = collection.nextItemIndex,
                owner = collection.owner,
                name = metadata.name.orEmpty(),
                description = metadata.description.orEmpty(),
                image = metadata.image,
                imageData = metadata.imageData ?: byteArrayOf(),
                coverImage = metadata.coverImage,
                coverImageData = metadata.coverImageData ?: byteArrayOf(),
            )

            // Trigger other jobs
            (new.owner as? AddrStd)?.let { accounts.tryEmitNext(it) }

            royalties.tryEmitNext(new.address)

            collectionRepository.save(new).awaitSingleOrNull()
        }
    }

    fun processMissingItems(collection: CollectionModel) = mono {
        (0 until collection.nextItemIndex)
            .toFlux()
            .filterWhen { index ->
                // Ignore items that are already added and indexed
                itemRepository.existsByIndexAndCollection(index, collection.address).not()
            }
            .concatMap { index ->
                mono {
                    logger.debug(
                        "fetching missing collection {} item no. {}",
                        value("collection", collection.address),
                        value("index", index)
                    )
                    NFTCollection.itemAddressOf(collection.address, index, liteApi, referenceBlock) as? AddrStd
                    // If not addrstd, this is null and we just skip it
                }
            }
            .filterWhen { itemRepository.existsById(it).not() } // Isn't in the repository?
            .doOnNext {
                items.tryEmitNext(it)
            }
            .then()
            .awaitSingleOrNull()
    }

    fun processItem(address: AddrStd) = mono {
        itemRepository.findById(address).awaitSingleOrNull()?.let { dbItem ->
            if (Duration.between(dbItem.updated, Instant.now()) > configuration.itemUpdatePeriod) {
                logger.debug("updating existing item {} blockchain data", value("address", dbItem.address))

                val item = NFTItem.of(dbItem.address, liteApi, referenceBlock)

                var new = dbItem.copy(
                    initialized = item.initialized,
                    index = item.index,
                    collection = item.collection,
                    owner = item.owner,
                    updated = Instant.now(),
                )

                if (Duration.between(dbItem.metadataUpdated, Instant.now()) > configuration.itemMetadataUpdatePeriod) {
                    logger.debug("updating existing item {} metadata", value("address", dbItem.address))
                    val metadata = item.metadata(liteApi, referenceBlock)

                    new = new.copy(
                        name = metadata.name.orEmpty(),
                        description = metadata.description.orEmpty(),
                        image = metadata.image,
                        imageData = metadata.imageData ?: byteArrayOf(),
                        metadataUpdated = Instant.now()
                    )
                } else {
                    logger.debug(
                        "item {} metadata is up-to-date, last updated {}",
                        value("address", dbItem.address),
                        value("updated", dbItem.metadataUpdated)
                    )
                }

                // Trigger other jobs
                (dbItem.owner as? AddrStd)?.let { accounts.tryEmitNext(it); sales.tryEmitNext(it) }
                if (dbItem.owner != new.owner) // In case owner was changed, update both
                    (new.owner as? AddrStd)?.let { accounts.tryEmitNext(it); sales.tryEmitNext(it) }

                (new.collection as? AddrStd)?.let { collections.tryEmitNext(it) }
                if (new.collection is AddrNone)
                    royalties.tryEmitNext(new.address)

                itemRepository.update(new).awaitSingleOrNull()
            } else {
                logger.debug(
                    "item {} blockchain data is up-to-date, last updated {}",
                    value("address", dbItem.address),
                    value("updated", dbItem.updated)
                )
                dbItem
            }
        } ?: run {
            logger.debug("saving new item {}", value("address", address))

            val item = NFTItem.of(address, liteApi, referenceBlock)
            val metadata = item.metadata(liteApi, referenceBlock)

            val new = ItemModel(
                address = item.address as AddrStd,
                initialized = item.initialized,
                index = item.index,
                collection = item.collection,
                owner = item.owner,
                name = metadata.name.orEmpty(),
                description = metadata.description.orEmpty(),
                image = metadata.image,
                imageData = metadata.imageData ?: byteArrayOf(),
            )

            // Trigger other jobs
            (new.owner as? AddrStd)?.let { accounts.tryEmitNext(it); sales.tryEmitNext(it) }

            (new.collection as? AddrStd)?.let { collections.tryEmitNext(it); royalties.tryEmitNext(it) }
            if (new.collection is AddrNone)
                royalties.tryEmitNext(new.address)

            itemRepository.save(new).awaitSingleOrNull()
        }
    }

    fun processRoyalty(address: AddrStd) = mono {
        royaltyRepository.findById(address).awaitSingleOrNull()?.let { dbRoyalty ->
            if (Duration.between(dbRoyalty.updated, Instant.now()) > configuration.royaltyUpdatePeriod) {
                logger.debug("updating existing royalty {} blockchain data", value("address", dbRoyalty.address))

                try {
                    val royalty = NFTRoyalty.of(dbRoyalty.address, liteApi, referenceBlock)
                    var new = dbRoyalty.copy(
                        numerator = royalty.numerator,
                        denominator = royalty.denominator,
                        destination = royalty.destination,
                        updated = Instant.now(),
                    )

                    // Trigger other jobs
                    (dbRoyalty.destination as? AddrStd)?.let { accounts.tryEmitNext(it) }
                    if (dbRoyalty.destination != new.destination) // In case destination was changed, update both
                        (new.destination as? AddrStd)?.let { accounts.tryEmitNext(it) }

                    royaltyRepository.update(new).awaitSingleOrNull()
                } catch (e: NFTException) {
                    logger.error(
                        "WHAT THE FUCKING FUCK - couldn't get royalty for {} but record is in the database. Something's fishy",
                        value("address", dbRoyalty.address),
                        e
                    )
                    Exceptions.propagate(e)
                    null
                }
            } else {
                logger.debug(
                    "royalty {} blockchain data is up-to-date, last updated {}",
                    value("address", dbRoyalty.address),
                    value("updated", dbRoyalty.updated)
                )
                dbRoyalty
            }
        } ?: run {
            try {
                val royalty = NFTRoyalty.of(address, liteApi, referenceBlock)

                logger.debug("saving new royalty {}", value("address", address))

                val new = RoyaltyModel(
                    address = royalty.address as AddrStd,
                    numerator = royalty.numerator,
                    denominator = royalty.denominator,
                    destination = royalty.destination,
                )
                royaltyRepository.save(new).awaitSingleOrNull()
            } catch (e: NFTException) {
                logger.info("contract {} doesn't implement royalty extension", value("address", address), e)
                Exceptions.propagate(e)
                null
            }
        }
    }

    fun processSale(address: AddrStd) = mono {
        saleRepository.findById(address).awaitSingleOrNull()?.let { dbSale ->
            if (Duration.between(dbSale.updated, Instant.now()) > configuration.saleUpdatePeriod) {
                logger.debug("updating existing sale {} blockchain data", value("address", dbSale.address))

                try {
                    val sale = NFTSale.of(dbSale.address, liteApi, referenceBlock)
                    var new = dbSale.copy(
                        marketplace = sale.marketplace,
                        item = sale.item,
                        owner = sale.owner,
                        fullPrice = sale.fullPrice,
                        marketplaceFee = sale.marketplaceFee,
                        royalty = sale.royalty,
                        royaltyDestination = sale.royaltyDestination,
                        updated = Instant.now(),
                    )

                    // Trigger other jobs
                    (dbSale.item as? AddrStd)?.let { items.tryEmitNext(it) }
                    (dbSale.owner as? AddrStd)?.let { accounts.tryEmitNext(it) }
                    if (dbSale.owner != new.owner) // In case owner was changed, update both
                        (new.owner as? AddrStd)?.let { accounts.tryEmitNext(it) }
                    (dbSale.royaltyDestination as? AddrStd)?.let { accounts.tryEmitNext(it) }
                    if (dbSale.royaltyDestination != new.royaltyDestination) // In case destination was changed, update both
                        (new.royaltyDestination as? AddrStd)?.let { accounts.tryEmitNext(it) }

                    saleRepository.update(new).awaitSingleOrNull()
                } catch (e: NFTException) {
                    logger.info(
                        "couldn't get sale {} information, item was most likely sold",
                        value("address", dbSale.address),
                        e
                    )
                    Exceptions.propagate(e)
                    null
                }
            } else {
                logger.debug(
                    "sale {} blockchain data is up-to-date, last updated {}",
                    value("address", dbSale.address),
                    value("updated", dbSale.updated)
                )
                dbSale
            }
        } ?: run {
            try {
                val sale = NFTSale.of(address, liteApi, referenceBlock)

                logger.debug("saving new sale {}", value("address", address))

                val new = SaleModel(
                    address = sale.address as AddrStd,
                    marketplace = sale.marketplace,
                    item = sale.item,
                    owner = sale.owner,
                    fullPrice = sale.fullPrice,
                    marketplaceFee = sale.marketplaceFee,
                    royalty = sale.royalty,
                    royaltyDestination = sale.royaltyDestination,
                )
                saleRepository.save(new).awaitSingleOrNull()
            } catch (e: NFTException) {
                logger.info(
                    "contract {} doesn't implement sales. Must be a regular user account",
                    value("address", address),
                    e
                )
                accounts.tryEmitNext(address)
                Exceptions.propagate(e)
                null
            }
        }
    }

    companion object : KLogging()
}
