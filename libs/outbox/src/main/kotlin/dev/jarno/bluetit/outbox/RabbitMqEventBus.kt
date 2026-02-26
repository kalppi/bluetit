package dev.jarno.bluetit.outbox

import org.slf4j.LoggerFactory
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
        rabbitTemplate.convertAndSend(
            "events.exchange",
            eventType,
            payloadJson,
        )
        logger.info("Successfully published to RabbitMQ - eventType: {}, aggregateId: {}", eventType, aggregateId)
    }
}

