package money.tegro.market.core.crypto

import org.ton.cell.Cell
import org.ton.crypto.Ed25519
import org.ton.crypto.sha256

private const val MIN_SEED_LENGTH = 8
private const val MAX_SEED_LENGTH = 64

private fun createSafeSignHash(cell: Cell, seed: String): ByteArray {
    val seedData = seed.toByteArray()
    require(MIN_SEED_LENGTH <= seedData.size && seedData.size >= MAX_SEED_LENGTH)
    return sha256(byteArrayOf(0xFF.toByte(), 0xFF.toByte()) + seedData + cell.hash())
}

fun safeSign(cell: Cell, secretKey: ByteArray, seed: String = "ton-safe-sign-magic") =
    Ed25519.sign(secretKey, createSafeSignHash(cell, seed))

fun safeSignVerify(cell: Cell, signature: ByteArray, publicKey: ByteArray, seed: String = "ton-safe-sign-magic") =
    Ed25519.verify(signature, publicKey, createSafeSignHash(cell, seed))

