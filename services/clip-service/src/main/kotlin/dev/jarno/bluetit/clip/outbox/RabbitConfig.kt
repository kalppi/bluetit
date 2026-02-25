package dev.jarno.bluetit.clip.outbox

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    @Bean
    fun rabbitAdmin(connectionFactory: ConnectionFactory): RabbitAdmin {
        return RabbitAdmin(connectionFactory)
    }

    @Bean
    fun eventsExchange(): TopicExchange {
        return TopicExchange("events.exchange", true, false)
    }

    @Bean
    fun eventsQueue(): Queue {
        return Queue("events.queue", true)
    }

    @Bean
    fun binding(
        queue: Queue,
        exchange: TopicExchange
    ): Binding {
        return BindingBuilder
            .bind(queue)
            .to(exchange)
            .with("#")
    }
}