package money.tegro.market.factory

import io.micrometer.core.instrument.MeterRegistry
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import mu.KLogging
import org.ton.block.Block
import org.ton.lite.client.LiteClient

@Factory
open class LiveBlockFactory(
    private val registry: MeterRegistry,
    private val liteClient: LiteClient
) {
    @Singleton
    fun liveBlocks(): Flow<Block> = flow {
        while (currentCoroutineContext().isActive)
            liteClient.getBlock(liteClient.getLastBlockId())?.let { emit(it) }
    }
        .distinctUntilChanged()
        .shareIn(CoroutineScope(Dispatchers.Default), SharingStarted.Lazily, 100)

    companion object : KLogging()
}
