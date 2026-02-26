package dev.jarno.bluetit.outbox

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class RabbitMqEventBus(
    private val rabbitTemplate: RabbitTemplate,
) : EventBus {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.warn("RabbitMqEventBus instance created - hashCode: {}, rabbitTemplate: {}",
            System.identityHashCode(this), System.identityHashCode(rabbitTemplate))
    }

    override fun publish(eventType: String, payloadJson: String, aggregateId: String) {
        logger.info("Publishing to RabbitMQ - eventType: {}, aggregateId: {}, payload: {}", eventType, aggregateId, payloadJson)

        // Create message properties with correct content type
        val messageProperties = MessageProperties().apply {
            contentType = "application/json"
            contentEncoding = "UTF-8"
        }

        // Create message with raw JSON bytes (no double serialization)
        val message = Message(payloadJson.toByteArray(Charsets.UTF_8), messageProperties)

        // Use send() instead of convertAndSend() to avoid double serialization
        rabbitTemplate.send(
            "events.exchange",
            eventType,
            message
        )

        logger.info("Successfully published to RabbitMQ - eventType: {}, aggregateId: {}", eventType, aggregateId)
    }
}

