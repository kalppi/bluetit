package dev.jarno.bluetit.orchestrator.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMqConfig {

    @Bean
    fun clipRequestedQueue(): Queue {
        return Queue("clip-requested-queue", true)
    }

    @Bean
    fun eventsExchange(): TopicExchange {
        return TopicExchange("events.exchange")
    }

    @Bean
    fun clipRequestedBinding(clipRequestedQueue: Queue, eventsExchange: TopicExchange): Binding {
        return BindingBuilder
            .bind(clipRequestedQueue)
            .to(eventsExchange)
            .with("ClipRequested")
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

