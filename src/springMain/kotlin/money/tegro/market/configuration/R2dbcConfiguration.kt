package money.tegro.market.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.PostgresDialect
import org.ton.block.MsgAddressInt


@Configuration
class R2dbcConfiguration {
    @Bean
    fun customConversions(
        msgAddressInt2ByteArrayConverter: Converter<MsgAddressInt, ByteArray>,
        byteArray2MsgAddressIntConverter: Converter<ByteArray, MsgAddressInt>,
    ) =
        R2dbcCustomConversions.of(
            PostgresDialect.INSTANCE,
            msgAddressInt2ByteArrayConverter,
            byteArray2MsgAddressIntConverter,
        )
}
