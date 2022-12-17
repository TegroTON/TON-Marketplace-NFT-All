package money.tegro.market.web

import com.ionspin.kotlin.bignum.integer.BigInteger
import io.ktor.util.*
import io.ktor.utils.io.core.*
import kotlin.experimental.and
import kotlin.experimental.or

fun BigInteger.formatTON() = this.toString()
    .let {
        it.dropLast(9).ifEmpty { "0" } + "." +
                it.takeLast(9).padStart(9, '0').dropLastWhile { it == '0' }.padEnd(2, '0')
    }

fun isValidAddress(address: String) = try {
    parseAddress(address)
    true
} catch (_: Exception) {
    false
}

@OptIn(ExperimentalUnsignedTypes::class)
fun parseAddress(address: String): Pair<Int, ByteArray> {
    if (address.contains(':')) {
        // 32 bytes, each represented as 2 characters
        require(address.substringAfter(':').length == 32 * 2)
        return address.substringBefore(':').toByte().toInt() to hex(address.substringAfter(':'))
    } else {
        val packet = ByteReadPacket(
            try {
                base64url(address)
            } catch (e: Exception) {
                try {
                    base64(address)
                } catch (e: Exception) {
                    throw IllegalArgumentException("Can't parse address: $address", e)
                }
            }
        )

        require(packet.remaining == 36L) { "invalid byte-array size expected: 36, actual: ${packet.remaining}" }
        // not 0x80 = 0x7F; here we clean the test only flag to only check proper bounce flags
        val tag = packet.readByte()
        val workchain = packet.readByte().toInt()
        val rawAddress = packet.readBytes(32)
        val cleanTestOnly = tag and 0x7F.toByte()
        check((cleanTestOnly == 0x11.toByte()) or (cleanTestOnly == 0x51.toByte())) {
            "unknown address tag"
        }

        val expectedChecksum = packet.readUShort().toInt()
        val actualChecksum = checksum(tag, workchain, rawAddress)
        check(expectedChecksum == actualChecksum) {
            "CRC check failed"
        }

        return workchain to rawAddress
    }
}

private fun checksum(tag: Byte, workchainId: Int, address: ByteArray): Int =
    crc16(byteArrayOf(tag, workchainId.toByte()), address)

// Get the tag byte based on set flags
private fun tag(testOnly: Boolean, bounceable: Boolean): Byte =
    (if (testOnly) 0x80.toByte() else 0.toByte()) or
            (if (bounceable) 0x11.toByte() else 0x51.toByte())

fun toUserFriendly(
    address: Pair<Int, ByteArray>,
    testOnly: Boolean = false,
    bounceable: Boolean = true,
    urlSafe: Boolean = true
): String {
    val tag = tag(testOnly, bounceable)
    val workchain = address.first
    val rawAddress = address.second
    val checksum = checksum(tag, workchain, rawAddress)

    val data = buildPacket {
        writeByte(tag)
        writeByte(workchain.toByte())
        writeFully(rawAddress)
        writeShort(checksum.toShort())
    }.readBytes()

    return if (urlSafe) {
        base64url(data)
    } else {
        base64(data)
    }
}

fun normalizeAddress(address: String): String = toUserFriendly(parseAddress(address))

fun normalizeAndShorten(address: String): String = normalizeAddress(address).let { it.take(6) + "..." + it.takeLast(6) }

inline fun jsObject(init: dynamic.() -> Unit): dynamic {
    val o = js("{}")
    init(o)
    return o
}

fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    val byteIterator = chunkedSequence(2)
        .map { it.toInt(16).toByte() }
        .iterator()

    return ByteArray(length / 2) { byteIterator.next() }
}

@OptIn(ExperimentalUnsignedTypes::class)
private val CRC16_TABLE = ushortArrayOf(
    0x0000u,
    0x1021u,
    0x2042u,
    0x3063u,
    0x4084u,
    0x50a5u,
    0x60c6u,
    0x70e7u,
    0x8108u,
    0x9129u,
    0xa14au,
    0xb16bu,
    0xc18cu,
    0xd1adu,
    0xe1ceu,
    0xf1efu,
    0x1231u,
    0x0210u,
    0x3273u,
    0x2252u,
    0x52b5u,
    0x4294u,
    0x72f7u,
    0x62d6u,
    0x9339u,
    0x8318u,
    0xb37bu,
    0xa35au,
    0xd3bdu,
    0xc39cu,
    0xf3ffu,
    0xe3deu,
    0x2462u,
    0x3443u,
    0x0420u,
    0x1401u,
    0x64e6u,
    0x74c7u,
    0x44a4u,
    0x5485u,
    0xa56au,
    0xb54bu,
    0x8528u,
    0x9509u,
    0xe5eeu,
    0xf5cfu,
    0xc5acu,
    0xd58du,
    0x3653u,
    0x2672u,
    0x1611u,
    0x0630u,
    0x76d7u,
    0x66f6u,
    0x5695u,
    0x46b4u,
    0xb75bu,
    0xa77au,
    0x9719u,
    0x8738u,
    0xf7dfu,
    0xe7feu,
    0xd79du,
    0xc7bcu,
    0x48c4u,
    0x58e5u,
    0x6886u,
    0x78a7u,
    0x0840u,
    0x1861u,
    0x2802u,
    0x3823u,
    0xc9ccu,
    0xd9edu,
    0xe98eu,
    0xf9afu,
    0x8948u,
    0x9969u,
    0xa90au,
    0xb92bu,
    0x5af5u,
    0x4ad4u,
    0x7ab7u,
    0x6a96u,
    0x1a71u,
    0x0a50u,
    0x3a33u,
    0x2a12u,
    0xdbfdu,
    0xcbdcu,
    0xfbbfu,
    0xeb9eu,
    0x9b79u,
    0x8b58u,
    0xbb3bu,
    0xab1au,
    0x6ca6u,
    0x7c87u,
    0x4ce4u,
    0x5cc5u,
    0x2c22u,
    0x3c03u,
    0x0c60u,
    0x1c41u,
    0xedaeu,
    0xfd8fu,
    0xcdecu,
    0xddcdu,
    0xad2au,
    0xbd0bu,
    0x8d68u,
    0x9d49u,
    0x7e97u,
    0x6eb6u,
    0x5ed5u,
    0x4ef4u,
    0x3e13u,
    0x2e32u,
    0x1e51u,
    0x0e70u,
    0xff9fu,
    0xefbeu,
    0xdfddu,
    0xcffcu,
    0xbf1bu,
    0xaf3au,
    0x9f59u,
    0x8f78u,
    0x9188u,
    0x81a9u,
    0xb1cau,
    0xa1ebu,
    0xd10cu,
    0xc12du,
    0xf14eu,
    0xe16fu,
    0x1080u,
    0x00a1u,
    0x30c2u,
    0x20e3u,
    0x5004u,
    0x4025u,
    0x7046u,
    0x6067u,
    0x83b9u,
    0x9398u,
    0xa3fbu,
    0xb3dau,
    0xc33du,
    0xd31cu,
    0xe37fu,
    0xf35eu,
    0x02b1u,
    0x1290u,
    0x22f3u,
    0x32d2u,
    0x4235u,
    0x5214u,
    0x6277u,
    0x7256u,
    0xb5eau,
    0xa5cbu,
    0x95a8u,
    0x8589u,
    0xf56eu,
    0xe54fu,
    0xd52cu,
    0xc50du,
    0x34e2u,
    0x24c3u,
    0x14a0u,
    0x0481u,
    0x7466u,
    0x6447u,
    0x5424u,
    0x4405u,
    0xa7dbu,
    0xb7fau,
    0x8799u,
    0x97b8u,
    0xe75fu,
    0xf77eu,
    0xc71du,
    0xd73cu,
    0x26d3u,
    0x36f2u,
    0x0691u,
    0x16b0u,
    0x6657u,
    0x7676u,
    0x4615u,
    0x5634u,
    0xd94cu,
    0xc96du,
    0xf90eu,
    0xe92fu,
    0x99c8u,
    0x89e9u,
    0xb98au,
    0xa9abu,
    0x5844u,
    0x4865u,
    0x7806u,
    0x6827u,
    0x18c0u,
    0x08e1u,
    0x3882u,
    0x28a3u,
    0xcb7du,
    0xdb5cu,
    0xeb3fu,
    0xfb1eu,
    0x8bf9u,
    0x9bd8u,
    0xabbbu,
    0xbb9au,
    0x4a75u,
    0x5a54u,
    0x6a37u,
    0x7a16u,
    0x0af1u,
    0x1ad0u,
    0x2ab3u,
    0x3a92u,
    0xfd2eu,
    0xed0fu,
    0xdd6cu,
    0xcd4du,
    0xbdaau,
    0xad8bu,
    0x9de8u,
    0x8dc9u,
    0x7c26u,
    0x6c07u,
    0x5c64u,
    0x4c45u,
    0x3ca2u,
    0x2c83u,
    0x1ce0u,
    0x0cc1u,
    0xef1fu,
    0xff3eu,
    0xcf5du,
    0xdf7cu,
    0xaf9bu,
    0xbfbau,
    0x8fd9u,
    0x9ff8u,
    0x6e17u,
    0x7e36u,
    0x4e55u,
    0x5e74u,
    0x2e93u,
    0x3eb2u,
    0x0ed1u,
    0x1ef0u
)

fun crc16(string: String): Int = crc16(string.encodeToByteArray())

@OptIn(ExperimentalUnsignedTypes::class)
fun crc16(vararg byteArrays: ByteArray): Int {
    var crc: UShort = 0u
    byteArrays.forEach { byteArray ->
        byteArray.forEach { byte ->
            val t = (byte.toInt() xor (crc.toInt() ushr 8)) and 0xff
            crc = CRC16_TABLE[t] xor (crc.toInt() shl 8).toUShort()
        }
    }
    return crc.toInt()
}

private val BASE64 =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".encodeToByteArray()
private val BASE64_URL =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".encodeToByteArray()

fun base64(string: String): ByteArray = string.commonDecodeBase64ToArray()
    ?: throw IllegalArgumentException("Can't decode string: '${string.replace("\n", "")}'")

fun base64(byteArray: ByteArray): String = byteArray.commonEncodeBse64(BASE64)

fun base64url(string: String): ByteArray = string.commonDecodeBase64ToArray()
    ?: throw IllegalArgumentException("Can't decode string: '${string.replace("\n", "")}'")

fun base64url(byteArray: ByteArray): String = byteArray.commonEncodeBse64(BASE64_URL)

private fun String.commonDecodeBase64ToArray(): ByteArray? {
    // Ignore trailing '=' padding and whitespace from the input.
    var limit = length
    while (limit > 0) {
        val c = this[limit - 1]
        if (c != '=' && c != '\n' && c != '\r' && c != ' ' && c != '\t') {
            break
        }
        limit--
    }

    // If the input includes whitespace, this output array will be longer than necessary.
    val out = ByteArray((limit * 6L / 8L).toInt())
    var outCount = 0
    var inCount = 0

    var word = 0
    for (pos in 0 until limit) {
        val c = this[pos]

        val bits: Int
        if (c in 'A'..'Z') {
            // char ASCII value
            //  A    65    0
            //  Z    90    25 (ASCII - 65)
            bits = c.code - 65
        } else if (c in 'a'..'z') {
            // char ASCII value
            //  a    97    26
            //  z    122   51 (ASCII - 71)
            bits = c.code - 71
        } else if (c in '0'..'9') {
            // char ASCII value
            //  0    48    52
            //  9    57    61 (ASCII + 4)
            bits = c.code + 4
        } else if (c == '+' || c == '-') {
            bits = 62
        } else if (c == '/' || c == '_') {
            bits = 63
        } else if (c == '\n' || c == '\r' || c == ' ' || c == '\t') {
            continue
        } else {
            return null
        }

        // Append this char's 6 bits to the word.
        word = word shl 6 or bits

        // For every 4 chars of input, we accumulate 24 bits of output. Emit 3 bytes.
        inCount++
        if (inCount % 4 == 0) {
            out[outCount++] = (word shr 16).toByte()
            out[outCount++] = (word shr 8).toByte()
            out[outCount++] = word.toByte()
        }
    }

    val lastWordChars = inCount % 4
    when (lastWordChars) {
        1 -> {
            // We read 1 char followed by "===". But 6 bits is a truncated byte! Fail.
            return null
        }

        2 -> {
            // We read 2 chars followed by "==". Emit 1 byte with 8 of those 12 bits.
            word = word shl 12
            out[outCount++] = (word shr 16).toByte()
        }

        3 -> {
            // We read 3 chars, followed by "=". Emit 2 bytes for 16 of those 18 bits.
            word = word shl 6
            out[outCount++] = (word shr 16).toByte()
            out[outCount++] = (word shr 8).toByte()
        }
    }

    // If we sized our out array perfectly, we're done.
    if (outCount == out.size) return out

    // Copy the decoded bytes to a new, right-sized array.
    return out.copyOf(outCount)
}

private fun ByteArray.commonEncodeBse64(map: ByteArray): String {
    val length = (size + 2) / 3 * 4
    val out = ByteArray(length)
    var index = 0
    val end = size - size % 3
    var i = 0
    while (i < end) {
        val b0 = this[i++].toInt()
        val b1 = this[i++].toInt()
        val b2 = this[i++].toInt()
        out[index++] = map[(b0 and 0xff shr 2)]
        out[index++] = map[(b0 and 0x03 shl 4) or (b1 and 0xff shr 4)]
        out[index++] = map[(b1 and 0x0f shl 2) or (b2 and 0xff shr 6)]
        out[index++] = map[(b2 and 0x3f)]
    }
    when (size - end) {
        1 -> {
            val b0 = this[i].toInt()
            out[index++] = map[b0 and 0xff shr 2]
            out[index++] = map[b0 and 0x03 shl 4]
            out[index++] = '='.code.toByte()
            out[index] = '='.code.toByte()
        }

        2 -> {
            val b0 = this[i++].toInt()
            val b1 = this[i].toInt()
            out[index++] = map[(b0 and 0xff shr 2)]
            out[index++] = map[(b0 and 0x03 shl 4) or (b1 and 0xff shr 4)]
            out[index++] = map[(b1 and 0x0f shl 2)]
            out[index] = '='.code.toByte()
        }
    }
    return out.decodeToString()
}
