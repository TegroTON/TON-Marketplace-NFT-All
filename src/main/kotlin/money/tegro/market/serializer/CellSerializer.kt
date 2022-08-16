package money.tegro.market.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.ton.boc.BagOfCells
import org.ton.cell.Cell
import org.ton.crypto.base64

class CellSerializer : StdSerializer<Cell>(Cell::class.java) {
    override fun serialize(value: Cell?, gen: JsonGenerator?, provider: SerializerProvider?) {
        gen?.writeString(value?.let { base64(BagOfCells(it).toByteArray()) })
    }
}
