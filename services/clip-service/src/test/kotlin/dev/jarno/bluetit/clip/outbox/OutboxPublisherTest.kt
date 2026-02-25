package dev.jarno.bluetit.clip.outbox

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.time.Instant
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OutboxPublisherTest {

    private val outboxJpa = mock<OutboxEventJpaRepository>()
    private val eventBus = mock<EventBus>()
    private val publisher = OutboxPublisher(outboxJpa, eventBus)

    @Test
    fun `publishBatch should do nothing when no unpublished events exist`() {
        // Given
        whenever(outboxJpa.findUnpublished()).thenReturn(emptyList())

        // When
        publisher.publishBatch()

        // Then
        verify(outboxJpa).findUnpublished()
        verifyNoInteractions(eventBus)
        verify(outboxJpa, never()).save(any())
    }

    @Test
    fun `publishBatch should publish all events in batch`() {
        // Given
        val event1 = createUnpublishedEvent("event.type.1", "aggregate-1")
        val event2 = createUnpublishedEvent("event.type.2", "aggregate-2")
        whenever(outboxJpa.findUnpublished()).thenReturn(listOf(event1, event2))

        // When
        publisher.publishBatch()

        // Then
        verify(eventBus).publish("event.type.1", """{"data":"test"}""", "aggregate-1")
        verify(eventBus).publish("event.type.2", """{"data":"test"}""", "aggregate-2")
        verify(outboxJpa, times(2)).save(any())
    }

    @Test
    fun `publishBatch should limit batch to 50 events`() {
        // Given
        val events = (1..100).map { createUnpublishedEvent("event.type.$it", "aggregate-$it") }
        whenever(outboxJpa.findUnpublished()).thenReturn(events)

        // When
        publisher.publishBatch()

        // Then
        verify(eventBus, times(50)).publish(any(), any(), any())
        verify(outboxJpa, times(50)).save(any())
    }

    @Test
    fun `publishOne should mark event as published on success`() {
        // Given
        val event = createUnpublishedEvent("event.type", "aggregate-1")

        // When
        publisher.publishOne(event)

        // Then
        verify(eventBus).publish("event.type", """{"data":"test"}""", "aggregate-1")
        assertNotNull(event.publishedAt)
        assertEquals(1, event.attemptCount)
        assertNull(event.lastError)
        verify(outboxJpa).save(event)
    }

    @Test
    fun `publishOne should increment attempt count on success`() {
        // Given
        val event = createUnpublishedEvent("event.type", "aggregate-1")
        event.attemptCount = 3

        // When
        publisher.publishOne(event)

        // Then
        assertEquals(4, event.attemptCount)
        verify(outboxJpa).save(event)
    }

    @Test
    fun `publishOne should record error when event bus throws exception`() {
        // Given
        val event = createUnpublishedEvent("event.type", "aggregate-1")
        whenever(eventBus.publish(any(), any(), any()))
            .thenThrow(RuntimeException("Connection failed"))

        // When
        publisher.publishOne(event)

        // Then
        assertNull(event.publishedAt)
        assertEquals(1, event.attemptCount)
        assertEquals("Connection failed", event.lastError)
        verify(outboxJpa).save(event)
    }

    @Test
    fun `publishOne should not rethrow exception on failure`() {
        // Given
        val event = createUnpublishedEvent("event.type", "aggregate-1")
        whenever(eventBus.publish(any(), any(), any()))
            .thenThrow(RuntimeException("Connection failed"))

        // When & Then - should not throw
        publisher.publishOne(event)
    }

    @Test
    fun `publishBatch should continue processing remaining events when one fails`() {
        // Given
        val event1 = createUnpublishedEvent("event.type.1", "aggregate-1")
        val event2 = createUnpublishedEvent("event.type.2", "aggregate-2")
        val event3 = createUnpublishedEvent("event.type.3", "aggregate-3")

        whenever(outboxJpa.findUnpublished()).thenReturn(listOf(event1, event2, event3))
        whenever(eventBus.publish(eq("event.type.2"), any(), any()))
            .thenThrow(RuntimeException("Failed to publish"))

        // When
        publisher.publishBatch()

        // Then
        verify(eventBus).publish("event.type.1", """{"data":"test"}""", "aggregate-1")
        verify(eventBus).publish("event.type.2", """{"data":"test"}""", "aggregate-2")
        verify(eventBus).publish("event.type.3", """{"data":"test"}""", "aggregate-3")

        // All events should be saved (2 successful, 1 failed)
        verify(outboxJpa, times(3)).save(any())

        // Verify states
        assertNotNull(event1.publishedAt, "Event 1 should be published")
        assertNull(event2.publishedAt, "Event 2 should not be published")
        assertNotNull(event3.publishedAt, "Event 3 should be published")

        assertEquals("Failed to publish", event2.lastError)
    }

    @Test
    fun `publishOne should handle null error message`() {
        // Given
        val event = createUnpublishedEvent("event.type", "aggregate-1")
        whenever(eventBus.publish(any(), any(), any()))
            .thenThrow(RuntimeException())

        // When
        publisher.publishOne(event)

        // Then
        assertNull(event.publishedAt)
        assertEquals(1, event.attemptCount)
        assertNull(event.lastError)
        verify(outboxJpa).save(event)
    }

    @Test
    fun `publishOne should clear previous error on successful retry`() {
        // Given
        val event = createUnpublishedEvent("event.type", "aggregate-1")
        event.attemptCount = 2
        event.lastError = "Previous error"

        // When
        publisher.publishOne(event)

        // Then
        assertNotNull(event.publishedAt)
        assertEquals(3, event.attemptCount)
        assertNull(event.lastError)
        verify(outboxJpa).save(event)
    }

    private fun createUnpublishedEvent(
        eventType: String,
        aggregateId: String,
    ): OutboxEventEntity {
        return OutboxEventEntity(
            id = UUID.randomUUID(),
            aggregateType = "TestAggregate",
            aggregateId = aggregateId,
            eventType = eventType,
            payloadJson = """{"data":"test"}""",
            occurredAt = Instant.now(),
            publishedAt = null,
            attemptCount = 0,
            lastError = null,
        )
    }
}

