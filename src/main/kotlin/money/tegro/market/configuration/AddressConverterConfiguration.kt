package money.tegro.market.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.PostgresDialect
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.boc.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.tlb.parse
import org.ton.tlb.storeTlb

@Configuration
class AddressConverterConfiguration {
    @Bean
    fun msgAddressInt2ByteArrayConverter() =
        object : Converter<MsgAddressInt, ByteArray> {
            override fun convert(it: MsgAddressInt): ByteArray =
                BagOfCells(CellBuilder.createCell { storeTlb(MsgAddressInt, it) }).toByteArray()
        }

    @Bean
    fun byteArray2MsgAddressIntConverter() =
        object : Converter<ByteArray, MsgAddressInt> {
            override fun convert(it: ByteArray): MsgAddressInt =
                BagOfCells(it).roots.first().parse(MsgAddressInt)
        }


    @Bean
    fun msgAddress2ByteArrayConverter() =
        object : Converter<MsgAddress, ByteArray> {
            override fun convert(it: MsgAddress): ByteArray =
                BagOfCells(CellBuilder.createCell { storeTlb(MsgAddress, it) }).toByteArray()
        }

    @Bean
    fun byteArray2MsgAddressConverter() =
        object : Converter<ByteArray, MsgAddress> {
            override fun convert(it: ByteArray): MsgAddress =
                BagOfCells(it).roots.first().parse(MsgAddress)
        }

    @Bean
    fun addressConverters() =
        R2dbcCustomConversions.of(
            PostgresDialect.INSTANCE,
            msgAddressInt2ByteArrayConverter(),
            byteArray2MsgAddressIntConverter(),
            msgAddress2ByteArrayConverter(),
            byteArray2MsgAddressConverter()
        )
}
