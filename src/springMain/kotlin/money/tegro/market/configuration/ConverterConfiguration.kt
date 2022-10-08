package money.tegro.market.configuration

import money.tegro.market.toRaw
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.ton.block.AddrNone
import org.ton.block.Coins
import org.ton.block.MsgAddress
import org.ton.block.MsgAddressInt
import org.ton.boc.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.tlb.parse
import org.ton.tlb.storeTlb

@Configuration
class ConverterConfiguration {
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

    @Bean
    fun string2MsgAddressConverter() =
        object : Converter<String, MsgAddress> {
            override fun convert(it: String): MsgAddress =
                if (it.isBlank() || listOf("null", "none", "addr_none").contains(it.trim().lowercase())) {
                    AddrNone
                } else {
                    MsgAddressInt(it)
                }
        }

    @Bean
    fun msgAddressStringConverter() =
        object : Converter<MsgAddress, String> {
            override fun convert(it: MsgAddress): String? = it.toRaw()
        }

    @Bean
    fun coinsToStringConverter() =
        object : Converter<Coins, String> {
            override fun convert(it: Coins): String =
                it.amount.value.toString().let {
                    it.dropLast(9).ifEmpty { "0" } + "." +
                            it.takeLast(9).padStart(9, '0').dropLastWhile { it == '0' }
                }
        }
}
