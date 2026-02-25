package dev.jarno.bluetit.clip.outbox

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class RabbitMqEventBus(
    private val rabbitTemplate: RabbitTemplate,
) : EventBus {

    override fun publish(eventType: String, payloadJson: String, aggregateId: String) {
        rabbitTemplate.convertAndSend(
            "events.exchange",
            eventType,
            payloadJson,
        )
    }
}