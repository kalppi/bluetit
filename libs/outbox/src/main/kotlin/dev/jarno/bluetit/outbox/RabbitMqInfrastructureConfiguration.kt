package dev.jarno.bluetit.outbox

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqInfrastructureConfiguration {
    @Value("\${messaging.queue.clip-requested}")
    private lateinit var eventsQueueName: String

    @Value("\${messaging.exchange.events}")
    private lateinit var eventsExchangeName: String

    @Value("\${messaging.routing-key.clip-requested}")
    private lateinit var routingKey: String

    @Bean
    fun rabbitAdmin(connectionFactory: ConnectionFactory): RabbitAdmin {
        return RabbitAdmin(connectionFactory)
    }

    @Bean
    fun eventsExchange(): TopicExchange {
        return TopicExchange(eventsExchangeName, true, false)
    }

    @Bean
    fun eventsQueue(): Queue {
        return Queue(eventsQueueName, true, false, false)
    }

    @Bean
    fun binding(eventsQueue: Queue, eventsExchange: TopicExchange): Binding {
        return BindingBuilder.bind(eventsQueue)
            .to(eventsExchange)
            .with(routingKey)
    }
}



