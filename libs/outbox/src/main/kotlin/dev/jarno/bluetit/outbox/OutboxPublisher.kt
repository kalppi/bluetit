package dev.jarno.bluetit.outbox

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class OutboxPublisher(
    private val outboxJpa: OutboxEventJpaRepository,
    private val eventBus: EventBus,
) {

    @Scheduled(fixedDelayString = "\${outbox.publisher.delay-ms:1000}")
    fun publishBatch() {
        val batch = outboxJpa.findUnpublished().take(50)
        if (batch.isEmpty()) {
            return
        }

        batch.forEach { publishOne(it) }
    }

    fun publishOne(event: OutboxEventEntity) {
        try {
            eventBus.publish(
                eventType = event.eventType,
                payloadJson = event.payloadJson,
                aggregateId = event.aggregateId,
            )

            markAsPublished(event)
        } catch (e: Exception) {
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

