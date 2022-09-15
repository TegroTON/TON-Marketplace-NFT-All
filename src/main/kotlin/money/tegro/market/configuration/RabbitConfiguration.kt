package money.tegro.market.configuration

import mu.KLogging
import org.springframework.amqp.core.*
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
    fun queue(): Queue = QueueBuilder.durable("market.blocks").autoDelete().build()

    @Bean
    fun exchange(): Exchange = ExchangeBuilder.topicExchange("blocks").durable(true).build<TopicExchange>()

    @Bean
    fun binding(queue: Queue, exchange: Exchange): Binding =
        BindingBuilder.bind(queue).to(exchange).with("#").noargs()

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

//    @RabbitListener(queues = ["market.blocks"])
//    fun blocks(block: Block) {
//        logger.info("workchain ${block.info.shard.workchain_id} seqno ${block.info.seq_no}")
//    }

    companion object : KLogging()
}
