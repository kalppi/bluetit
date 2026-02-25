package dev.jarno.bluetit.orchestrator.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfig {
    @Value("\${messaging.queue.clip-requested}")
    private lateinit var eventsQueueName: String

    @Value("\${messaging.exchange.events}")
    private lateinit var eventsExchangeName: String

    @Value("\${messaging.routing-key.clip-requested}")
    private lateinit var routingKey: String

    @Bean
    fun clipRequestedQueue(): Queue {
        return Queue(eventsQueueName, true)
    }

    @Bean
    fun eventsExchange(): TopicExchange {
        return TopicExchange(eventsExchangeName)
    }

    @Bean
    fun binding(clipRequestedQueue: Queue, eventsExchange: TopicExchange): Binding {
        return BindingBuilder
            .bind(clipRequestedQueue)
            .to(eventsExchange)
            .with(routingKey)
    }

    @Bean
    fun messageConverter(): JacksonJsonMessageConverter {
        return JacksonJsonMessageConverter()
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = messageConverter()
        return template
    }
}

