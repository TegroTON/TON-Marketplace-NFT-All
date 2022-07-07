package money.tegro.market.core.converter

import io.micronaut.core.convert.ConversionContext
import io.micronaut.data.model.runtime.convert.AttributeConverter
import jakarta.inject.Singleton
import org.ton.bitstring.BitString
import org.ton.block.AddrStd
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

@Singleton
class AddrStdAttributeConverter : AttributeConverter<AddrStd, ByteArray> {
    override fun convertToPersistedValue(entityValue: AddrStd?, context: ConversionContext): ByteArray? =
        entityValue?.let { CellBuilder.createCell { storeTlb(addrStdCodec, it) }.bits.toByteArray() }

    override fun convertToEntityValue(persistedValue: ByteArray?, context: ConversionContext): AddrStd? =
        persistedValue?.let { Cell(BitString(it)).beginParse().loadTlb(addrStdCodec) }  // TODO: proper parsing

    companion object {
        val addrStdCodec = AddrStd.tlbCodec()
    }
}
