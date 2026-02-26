package dev.jarno.bluetit.outbox

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(
    name = "outbox_events", indexes = [
        Index(
            name = "idx_unpublished_occurred",
            columnList = "published_at, occurred_at"
        )
    ]
)
class OutboxEventEntity(
    @Id
    @Column(nullable = false, updatable = false)
    var id: UUID,

    @Column(name = "aggregate_type", nullable = false)
    var aggregateType: String,

    @Column(name = "aggregate_id", nullable = false)
    var aggregateId: String,

    @Column(name = "event_type", nullable = false)
    var eventType: String,

    @Column(name = "payload_json", nullable = false, columnDefinition = "text")
    var payloadJson: String,

    @Column(name = "occurred_at", nullable = false)
    var occurredAt: Instant,

    @Column(name = "published_at", nullable = true)
    var publishedAt: Instant?,

    @Column(name = "attempt_count", nullable = false)
    var attemptCount: Int,

    @Column(name = "last_error", nullable = true, columnDefinition = "text")
    var lastError: String?,
)

