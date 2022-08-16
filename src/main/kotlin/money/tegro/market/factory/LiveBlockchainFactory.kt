package money.tegro.market.factory

import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import money.tegro.market.core.toRaw
import mu.KLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.api.tonnode.TonNodeBlockId
import org.ton.bigint.BigInt
import org.ton.bitstring.BitString
import org.ton.block.AddrStd
import org.ton.block.Block
import org.ton.lite.client.LiteClient

@OptIn(FlowPreview::class)
@Configuration
class LiveBlockchainFactory(
    private val registry: MeterRegistry,
    private val liteClient: LiteClient
) {
    @Bean
    fun liveBlocks(): Flow<Block> =
        flow {
            while (currentCoroutineContext().isActive) {
                emit(liteClient.getLastBlockId()) // Masterchain blocks
            }
        }
            .distinctUntilChanged()
            .mapNotNull {
                try {
                    liteClient.getBlock(it)?.let(::listOf)
                } catch (e: Exception) {
                    logger.warn("couldn't get masterchain block {}", kv("seqno", it), e)
                    null
                }
            }
            .runningReduce { accumulator, value ->
                val lastMcShards = accumulator.last().extra.custom.value?.shard_hashes
                    ?.nodes()
                    .orEmpty()
                    .associate { BigInt(it.first.toByteArray()).toInt() to it.second.nodes().maxBy { it.seq_no } }

                value.last().extra.custom.value?.shard_hashes
                    ?.nodes()
                    .orEmpty()
                    .associate { BigInt(it.first.toByteArray()).toInt() to it.second.nodes().maxBy { it.seq_no } }
                    .flatMap { curr ->
                        (lastMcShards.getOrDefault(curr.key, curr.value).seq_no + 1u..curr.value.seq_no)
                            .map {
                                TonNodeBlockId(
                                    curr.key,
                                    curr.value.next_validator_shard.toLong() /* Shard.ID_ALL */,
                                    it.toInt()
                                )
                            } // TODO
                    }
                    .mapNotNull {
                        try {
                            liteClient.lookupBlock(it)?.let { liteClient.getBlock(it) }
                        } catch (e: Exception) {
                            logger.warn("couldn't get block {} {}", kv("workchain", it.workchain), kv("seqno", it), e)
                            null
                        }
                    }
                    .plus(value)
            }
            .flatMapConcat { it.asFlow() }
            .onEach {
                logger.debug(
                    "block {} {}",
                    kv("workchain", it.info.shard.workchain_id),
                    kv("seqno", it.info.seq_no)
                )
            }
            .shareIn(CoroutineScope(Dispatchers.Default), SharingStarted.Lazily, 100)

    @Bean
    fun liveAccounts(blocks: Flow<Block>): Flow<AddrStd> =
        blocks
            .flatMapConcat { block ->
                block.extra.account_blocks.nodes()
                    .flatMap {
                        sequenceOf(AddrStd(block.info.shard.workchain_id, it.first.account_addr))
                            .plus(it.first.transactions.nodes().map {
                                AddrStd(block.info.shard.workchain_id, it.first.account_addr)
                            })
                            .distinct()
                    }
                    .asFlow()
            }
            .filter { it !in SYSTEM_ADDRESSES }
            .onEach {
                registry.counter("live.account.affected").increment()

                logger.debug("affected account {}", kv("address", it.toRaw()))
            }
            .shareIn(CoroutineScope(Dispatchers.Default), SharingStarted.Lazily, 100)

    companion object : KLogging() {
        val SYSTEM_ADDRESSES = listOf(
            AddrStd(-1, BitString.of("5555555555555555555555555555555555555555555555555555555555555555")),
            AddrStd(-1, BitString.of("3333333333333333333333333333333333333333333333333333333333333333")),
            AddrStd(-1, BitString.of("0000000000000000000000000000000000000000000000000000000000000000")),
        )
    }
}
