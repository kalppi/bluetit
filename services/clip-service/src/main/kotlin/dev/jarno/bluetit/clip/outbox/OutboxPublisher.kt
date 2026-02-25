package dev.jarno.bluetit.clip.outbox

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

        for (event in batch) {
            publishOne(event)
        }
    }

    fun publishOne(event: OutboxEventEntity) {
        try {
            eventBus.publish(
                eventType = event.eventType,
                payloadJson = event.payloadJson,
                aggregateId = event.aggregateId,
            )

            event.publishedAt = Instant.now()
            event.attemptCount = event.attemptCount + 1
            event.lastError = null
            outboxJpa.save(event)
        } catch (e: Exception) {
            event.attemptCount = event.attemptCount + 1
            event.lastError = e.message
            outboxJpa.save(event)
        }
    }
}