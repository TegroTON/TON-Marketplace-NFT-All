package money.tegro.market.converters

import money.tegro.market.core.toRaw
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.ton.block.MsgAddressInt
import org.ton.boc.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.tlb.parse
import org.ton.tlb.storeTlb

@Configuration
class MsgAddressIntConverters {
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
    fun string2MsgAddressIntConverter() =
        object : Converter<String, MsgAddressInt> {
            override fun convert(it: String): MsgAddressInt = MsgAddressInt(it)
        }

    @Bean
    fun msgAddressInt2StringConverter() =
        object : Converter<MsgAddressInt, String> {
            override fun convert(it: MsgAddressInt): String = it.toRaw()
        }
}
