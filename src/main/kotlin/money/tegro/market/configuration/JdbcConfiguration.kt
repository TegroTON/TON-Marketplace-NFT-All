package money.tegro.market.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import org.ton.block.MsgAddressInt


@Configuration
class JdbcConfiguration(
    private val msgAddressInt2ByteArrayConverter: Converter<MsgAddressInt, ByteArray>,
    private val byteArray2MsgAddressIntConverter: Converter<ByteArray, MsgAddressInt>,
) : AbstractJdbcConfiguration() {
    override fun userConverters(): MutableList<*> =
        mutableListOf(msgAddressInt2ByteArrayConverter, byteArray2MsgAddressIntConverter)
}
