package dev.jarno.bluetit.clip.outbox

import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.time.Instant
import java.util.UUID

@Component
class OutboxWriter(
    private val jpa: OutboxEventJpaRepository,
    private val objectMapper: ObjectMapper,
) {

    fun add(
        aggregateType: String,
        aggregateId: String,
        eventType: String,
        payload: OutboxPayload,
    ) {
        val json = objectMapper.writeValueAsString(payload)

        val entity = OutboxEventEntity(
            id = UUID.randomUUID(),
            aggregateType = aggregateType,
            aggregateId = aggregateId,
            eventType = eventType,
            payloadJson = json,
            occurredAt = Instant.now(),
            publishedAt = null,
            attemptCount = 0,
            lastError = null,
        )

        jpa.save(entity)
    }
}