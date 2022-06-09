package money.tegro.market.nightcrawler

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.subscribe
import kotlinx.datetime.Clock
import money.tegro.market.db.CollectionEntity
import money.tegro.market.db.ItemAttributeEntity
import money.tegro.market.db.ItemEntity
import money.tegro.market.nft.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import org.ton.block.MsgAddressIntStd

fun Observable<NFTCollection>.upsertCollectionData() =
    this.subscribe {
        transaction {
            val collection = CollectionEntity.find(it.address).firstOrNull() ?: CollectionEntity.new {
                discovered = Clock.System.now()
                workchain = it.address.workchainId
                address = it.address.address.toByteArray()
            }

            collection.run {
                ownerWorkchain = it.owner.workchainId
                ownerAddress = it.owner.address.toByteArray()
                nextItemIndex = it.nextItemIndex

                dataLastIndexed = Clock.System.now()
            }
        }
    }

fun Observable<NFTItem>.upsertItemData() =
    this.subscribe {
        transaction {
            val item = ItemEntity.find(it.address).firstOrNull() ?: ItemEntity.new {
                discovered = Clock.System.now()
                workchain = it.address.workchainId
                address = it.address.address.toByteArray()
            }

            item.run {
                initialized = it is NFTItemInitialized

                if (it is NFTItemInitialized) {
                    index = it.index
                    this.collection = it.collection?.let { CollectionEntity.find(it).firstOrNull() }

                    ownerWorkchain = it.owner.workchainId
                    ownerAddress = it.owner.address.toByteArray()
                }

                dataLastIndexed = Clock.System.now()
            }
        }
    }

fun Observable<Pair<MsgAddressIntStd, NFTRoyalty?>>.updateRoyalty() =
    this.subscribe {
        val (address, royalty) = it
        transaction {
            // If not in database, do nothing - since we don't know whether to add new item or collection in this case
            (ItemEntity.find(address).firstOrNull() ?: CollectionEntity.find(address).firstOrNull())?.run {
                royaltyNumerator = royalty?.numerator
                royaltyDenominator = royalty?.denominator
                royaltyDestinationWorkchain = royalty?.destination?.workchainId
                royaltyDestinationAddress = royalty?.destination?.address?.toByteArray()

                royaltyLastIndexed = Clock.System.now()
            }
        }
    }

fun Observable<Pair<MsgAddressIntStd, NFTSale?>>.upsertItemSale() =
    this.subscribe {
        val (address, sale) = it
        transaction {
            val item = ItemEntity.find(address).firstOrNull() ?: ItemEntity.new {
                discovered = Clock.System.now()
                workchain = address.workchainId
                this.address = address.address.toByteArray()
            }

            item.run {
                marketplaceWorkchain = sale?.marketplace?.workchainId
                marketplaceAddress = sale?.marketplace?.address?.toByteArray()
                sellerWorkchain = sale?.owner?.workchainId
                sellerAddress = sale?.owner?.address?.toByteArray()
                price = sale?.price
                marketplaceFee = sale?.marketplaceFee

                saleRoyaltyDestinationWorkchain = sale?.royaltyDestination?.workchainId
                saleRoyaltyDestinationAddress = sale?.royaltyDestination?.address?.toByteArray()
                saleRoyalty = sale?.royalty

                ownerLastIndexed = Clock.System.now()
            }
        }
    }

fun Observable<Pair<MsgAddressIntStd, NFTCollectionMetadata?>>.upsertCollectionMetadata() =
    this.subscribe { it ->
        val (address, metadata) = it
        transaction {
            val collection = CollectionEntity.find(address).firstOrNull() ?: CollectionEntity.new {
                discovered = Clock.System.now()
                workchain = address.workchainId
                this.address = address.address.toByteArray()
            }

            collection.run {
                name = metadata?.name
                description = metadata?.description
                image = metadata?.image
                imageData = metadata?.imageData?.let { ExposedBlob(it) }
                coverImage = metadata?.coverImage
                coverImageData = metadata?.coverImageData?.let { ExposedBlob(it) }

                metadataLastIndexed = Clock.System.now()
            }
        }
    }


fun Observable<Pair<MsgAddressIntStd, NFTItemMetadata?>>.upsertItemMetadata() =
    this.subscribe { it ->
        val (address, metadata) = it
        transaction {
            val item = ItemEntity.find(address).firstOrNull() ?: ItemEntity.new {
                discovered = Clock.System.now()
                workchain = address.workchainId
                this.address = address.address.toByteArray()
            }

            item.run {
                name = metadata?.name
                description = metadata?.description
                image = metadata?.image
                imageData = metadata?.imageData?.let { ExposedBlob(it) }

                metadataLastIndexed = Clock.System.now()
            }

            metadata?.attributes.orEmpty().forEach { attribute ->
                transaction {
                    ItemAttributeEntity.find(item, attribute.trait).firstOrNull()?.run {
                        value = attribute.value
                    } ?: ItemAttributeEntity.new {
                        this.item = item
                        trait = attribute.trait
                        value = attribute.value
                    }
                }
            }
        }
    }
