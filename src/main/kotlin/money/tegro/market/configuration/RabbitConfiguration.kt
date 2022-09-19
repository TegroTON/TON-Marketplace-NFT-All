package money.tegro.market.configuration

import mu.KLogging
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageBuilder
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.AbstractMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.ton.block.Block
import org.ton.boc.BagOfCells
import org.ton.cell.CellBuilder
import org.ton.tlb.loadTlb
import org.ton.tlb.storeTlb

@Configuration
class RabbitConfiguration {
    @Bean
    fun messageConverter() = object : AbstractMessageConverter() {
        override fun createMessage(`object`: Any, messageProperties: MessageProperties): Message {
            val body = BagOfCells(CellBuilder.createCell { storeTlb(Block, `object` as Block) }).toByteArray()
            return MessageBuilder
                .withBody(body)
                .setContentTypeIfAbsentOrDefault(MessageProperties.CONTENT_TYPE_BYTES)
                .setContentLengthIfAbsent(body.size.toLong())
                .build()
        }

        override fun fromMessage(message: Message): Any =
            BagOfCells(message.body).roots.first().parse { loadTlb(Block) }
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, messageConverter: MessageConverter) =
        RabbitTemplate(connectionFactory).apply {
            setMessageConverter(messageConverter)
        }

    companion object : KLogging()
}
