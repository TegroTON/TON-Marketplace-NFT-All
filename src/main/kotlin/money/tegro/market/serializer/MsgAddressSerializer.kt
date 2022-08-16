package money.tegro.market.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import money.tegro.market.core.toRaw
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt

class MsgAddressSerializer : StdSerializer<MsgAddress>(MsgAddress::class.java) {
    override fun serialize(value: MsgAddress?, gen: JsonGenerator?, provider: SerializerProvider?) {
        if (value is MsgAddressInt) {
            gen?.writeString(value.toRaw())
        } else {
            gen?.writeNull()
        }
    }
}
