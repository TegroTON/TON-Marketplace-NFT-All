package money.tegro.market.core.converter

import io.micronaut.core.convert.ConversionContext
import io.micronaut.data.model.runtime.convert.AttributeConverter
import jakarta.inject.Singleton
import org.ton.bitstring.BitString
import org.ton.bitstring.EmptyBitString.toByteArray
import org.ton.block.MsgAddress
import org.ton.cell.Cell
import org.ton.cell.CellBuilder
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

@Singleton
class AddrStdAttributeConverter : AttributeConverter<MsgAddress, ByteArray> {
    override fun convertToPersistedValue(entityValue: MsgAddress?, context: ConversionContext): ByteArray? =
        entityValue?.let { CellBuilder.createCell { storeTlb(msgAddressCodec, it) }.toByteArray() }

    override fun convertToEntityValue(persistedValue: ByteArray?, context: ConversionContext): MsgAddress? =
        persistedValue?.let { Cell(BitString(it)).parse { loadTlb(msgAddressCodec) } }

    companion object {
        val msgAddressCodec = MsgAddress.tlbCodec()
    }
}
