package money.tegro.market.drive

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.filters.AuthenticationFetcher
import jakarta.inject.Singleton
import kotlinx.coroutines.reactor.mono
import money.tegro.market.core.crypto.safeSignVerify
import money.tegro.market.core.dto.WalletConfigDTO
import money.tegro.market.core.dto.toSafeBounceable
import org.reactivestreams.Publisher
import org.ton.block.AddrStd
import org.ton.block.Coins
import org.ton.block.MsgAddress
import org.ton.block.StateInit
import org.ton.cell.CellBuilder
import org.ton.cell.storeRef
import org.ton.crypto.base64
import org.ton.smartcontract.SmartContract
import org.ton.smartcontract.wallet.v4.WalletV4R2
import org.ton.tlb.storeTlb

@Singleton
class TonXAuthenticationFetcher : AuthenticationFetcher {
    private val mapper by lazy { jacksonObjectMapper() }
    private val coinsCodec by lazy { Coins.tlbCodec() }
    private val msgAddressCodec by lazy { MsgAddress.tlbCodec() }

    override fun fetchAuthentication(request: HttpRequest<*>?): Publisher<Authentication> = mono {
        request?.headers?.get(TONX_AUTH_HEADER)?.let { mapper.readValue(it, WalletConfigDTO::class.java) }
            ?.let { walletConfig ->
                // Check address
                val address = AddrStd(walletConfig.address)

                // Extract address from workchain, walletId and public key. Assume v4r2
                val extractedAddress = SmartContract.address(walletConfig.workchain, StateInit(
                    code = WalletV4R2.CODE,
                    data = CellBuilder.createCell {
                        storeUInt(0, 32) // seqno
                        storeUInt(walletConfig.walletId, 32)
                        storeBytes(base64(walletConfig.publicKey))
                        storeUInt(0, 1)
                    }
                ))

                require(extractedAddress == address)

                // Check signature
                val toSign = CellBuilder.createCell {
                    storeTlb(coinsCodec, Coins.ofNano(0L))
                    storeBytes(base64(walletConfig.session))
                    storeTlb(msgAddressCodec, address)
                    // endpoint
                    storeBit(true)
                    storeRef {
                        storeBytes(walletConfig.endpoint.toByteArray())
                    }
                    // app public key
                    storeRef {
                        storeBytes(base64(walletConfig.appPublicKey))
                    }
                }

                if (safeSignVerify(toSign, base64(walletConfig.walletSig), base64(walletConfig.publicKey))) {
                    // Successful
                    Authentication.build(address.toSafeBounceable())

                } else {
                    null
                }
            }
    }

    companion object {
        val TONX_AUTH_HEADER = "TONX_AUTH"
    }
}
