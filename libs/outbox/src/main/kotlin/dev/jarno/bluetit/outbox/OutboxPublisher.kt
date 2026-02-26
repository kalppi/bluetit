package dev.jarno.bluetit.outbox

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class OutboxPublisher(
    private val outboxJpa: OutboxEventJpaRepository,
    private val eventBus: EventBus,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        logger.warn("OutboxPublisher instance created - hashCode: {}", System.identityHashCode(this))
    }

    @Scheduled(fixedDelayString = "\${outbox.publisher.delay-ms:1000}")
    fun publishBatch() {
        val batch = outboxJpa.findUnpublished().take(50)
        if (batch.isEmpty()) {
            return
        }

        logger.info("Publishing batch of {} events from outbox", batch.size)
        batch.forEach { publishOne(it) }
        logger.info("Completed publishing batch of {} events", batch.size)
    }

    fun publishOne(event: OutboxEventEntity) {
        logger.info("Publishing event from outbox - id: {}, eventType: {}, aggregateId: {}, attemptCount: {}",
            event.id, event.eventType, event.aggregateId, event.attemptCount)
        try {
            eventBus.publish(
                eventType = event.eventType,
                payloadJson = event.payloadJson,
                aggregateId = event.aggregateId,
            )

            markAsPublished(event)
            logger.info("Successfully published event from outbox - id: {}, eventType: {}", event.id, event.eventType)
        } catch (e: Exception) {
            logger.error("Failed to publish event from outbox - id: {}, eventType: {}", event.id, event.eventType, e)
            markAsFailed(event, e)
        }
    }

    private fun markAsPublished(event: OutboxEventEntity) {
        event.publishedAt = Instant.now()
        event.attemptCount = event.attemptCount + 1
        event.lastError = null

        outboxJpa.save(event)
    }

    private fun markAsFailed(event: OutboxEventEntity, e: Exception) {
        event.attemptCount = event.attemptCount + 1
        event.lastError = e.message

        outboxJpa.save(event)
    }
}

