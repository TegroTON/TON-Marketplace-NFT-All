package money.tegro.market.contract

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import mu.KLogging
import org.ton.api.pk.PrivateKeyEd25519
import org.ton.bigint.BigInt
import org.ton.bitstring.BitString
import org.ton.block.*
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.cell.storeRef
import org.ton.crypto.SecureRandom
import org.ton.crypto.hex
import org.ton.hashmap.*
import org.ton.tlb.constructor.AnyTlbConstructor
import org.ton.tlb.storeTlb
import kotlin.time.Duration.Companion.seconds

data class HighloadWalletV2(
    private val private_key: PrivateKeyEd25519 = PrivateKeyEd25519.generate(),
    val subwallet_id: UInt = 0u,
    val workchain_id: Int = 0,
    val code: Cell = WALLET_CODE,
) {
    fun address() = AddrStd(workchain_id, CellBuilder.createCell { storeTlb(StateInit, stateInit()) }.hash())

    fun stateInit() = StateInit(code, createData())

    fun createData() = CellBuilder.createCell {
        storeUInt32(subwallet_id)
        storeUInt(0, 64) // last_cleaned
        storeBytes(private_key.publicKey().key)
        storeTlb(HashMapE.tlbCodec(16, AnyTlbConstructor), HashMapE.of())
    }

    fun createSignedMessage(builder: CellBuilder.() -> Unit): Cell = CellBuilder.createCell {
        val data = CellBuilder.createCell { apply(builder) }
        storeBytes(private_key.sign(data.hash()))
        storeBits(data.bits)
        storeRefs(data.refs)
    }

    fun createDeployMessage(query_id: ULong = generateQueryId()) = Message(
        ExtInMsgInfo(
            dest = address()
        ),
        stateInit(),
        createSignedMessage {
            storeUInt32(subwallet_id)
            storeUInt64(query_id)
            storeTlb(HashMapE.tlbCodec(16, AnyTlbConstructor), HashMapE.of())
        }
    )

    fun createTransferMessage(
        dest: MsgAddressInt,
        amount: Coins,
        payload: Cell = Cell.of(),
        bounce: Boolean = true,
        mode: Int = 3,
        stateInit: StateInit? = null,
    ) = CellBuilder.createCell {
        storeUInt(mode, 8)
        storeRef {
            storeTlb(
                MessageRelaxed.tlbCodec(AnyTlbConstructor), MessageRelaxed(
                    info = CommonMsgInfoRelaxed.IntMsgInfoRelaxed(
                        ihr_disabled = true,
                        bounce = bounce,
                        bounced = false,
                        src = AddrNone,
                        dest = dest,
                        value = CurrencyCollection(
                            coins = amount
                        )
                    ),
                    init = stateInit,
                    body = payload,
                )
            )
        }
    }

    fun createBundleTransferMessage(transfer: Cell, query_id: ULong = generateQueryId()) = Message(
        ExtInMsgInfo(
            dest = address()
        ),
        stateInit(),
        createSignedMessage {
            storeUInt32(subwallet_id)
            storeUInt64(query_id)
            storeTlb(
                HashMapE.tlbCodec(16, AnyTlbConstructor), RootHashMapE(
                    HashMapEdge(
                        label = HashMapLabel.of(BitString.of(16)),
                        node = HashMapNodeLeaf(transfer)
                    )
                )
            )
        }
    )

    companion object : KLogging() {
        val WALLET_CODE =
            BagOfCells(hex("B5EE9C724101090100E5000114FF00F4A413F4BCF2C80B010201200203020148040501EAF28308D71820D31FD33FF823AA1F5320B9F263ED44D0D31FD33FD3FFF404D153608040F40E6FA131F2605173BAF2A207F901541087F910F2A302F404D1F8007F8E16218010F4786FA5209802D307D43001FB009132E201B3E65B8325A1C840348040F4438AE63101C8CB1F13CB3FCBFFF400C9ED54080004D03002012006070017BD9CE76A26869AF98EB85FFC0041BE5F976A268698F98E99FE9FF98FA0268A91040207A0737D098C92DBFC95DD1F140034208040F4966FA56C122094305303B9DE2093333601926C21E2B39F9E545A")).roots.first()

        @JvmStatic
        fun generateQueryId(
            timeout: Instant = Clock.System.now() + 20.seconds,
            randomId: Int = SecureRandom.nextInt()
        ) =
            BigInt(timeout.epochSeconds).shl(32).add(BigInt(randomId)).toString().toULong()
    }
}
