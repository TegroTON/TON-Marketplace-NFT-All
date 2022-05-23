package money.tegro.market.nightcrawler

import kotlinx.coroutines.Dispatchers
import mu.KLogging
import org.ton.adnl.AdnlPublicKey
import org.ton.adnl.AdnlTcpClient
import org.ton.adnl.AdnlTcpClientImpl
import org.ton.lite.api.LiteApi
import org.ton.lite.api.liteserver.LiteServerError
import org.ton.tl.TlConstructor

class ResilientLiteClient(
    val adnlTcpClient: AdnlTcpClient,
    val retries: Int
) : LiteApi {
    constructor(
        host: String,
        port: Int,
        publicKey: ByteArray,
        retries: Int = 0
    ) : this(AdnlTcpClientImpl(host, port, AdnlPublicKey(publicKey), Dispatchers.Default), retries)

    suspend fun connect() = apply {
        adnlTcpClient.connect()
    }

    override suspend fun sendRawQuery(byteArray: ByteArray): ByteArray =
        adnlTcpClient.sendQuery(byteArray)

    override suspend fun <Q : Any, A : Any> sendQuery(
        query: Q,
        queryCodec: TlConstructor<Q>,
        answerCodec: TlConstructor<A>
    ): A {
        var tryNo = 1
        var savedError: LiteServerError
        do {
            try {
                return super.sendQuery(query, queryCodec, answerCodec)
            } catch (e: LiteServerError) {
                savedError = e
                logger.info("try no. $tryNo failed, trying again")
            }
            tryNo++
        } while ((retries == 0) || (tryNo <= retries))
        throw savedError
    }

    companion object : KLogging()
}

