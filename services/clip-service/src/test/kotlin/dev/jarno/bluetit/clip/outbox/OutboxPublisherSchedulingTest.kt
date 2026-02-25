package dev.jarno.bluetit.clip.outbox

import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.atLeast
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.Assertions.*

@SpringBootTest(
    classes = [OutboxPublisher::class]
)
@TestPropertySource(
    properties = [
        "outbox.publisher.delay-ms=100",
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
    ]
)
@EnableScheduling
class OutboxPublisherSchedulingTest {

    @Autowired
    private lateinit var publisher: OutboxPublisher

    @MockitoBean
    private lateinit var outboxJpa: OutboxEventJpaRepository

    @MockitoBean
    private lateinit var eventBus: EventBus

    @BeforeEach
    fun setUp() {
        reset(outboxJpa, eventBus)
    }

    @Test
    fun `scheduled task should execute periodically`() {
        // Given
        val event = createUnpublishedEvent()
        whenever(outboxJpa.findUnpublished()).thenReturn(listOf(event))

        // When - wait for scheduler to run at least 3 times
        await()
            .atMost(2, TimeUnit.SECONDS)
            .pollDelay(Duration.ofMillis(50))
            .untilAsserted {
                verify(outboxJpa, atLeast(3)).findUnpublished()
            }

        // Then
        verify(eventBus, atLeast(3)).publish(any(), any(), any())
    }

    @Test
    fun `scheduled task should process empty batches without error`() {
        // Given
        whenever(outboxJpa.findUnpublished()).thenReturn(emptyList())

        // When
        await()
            .atMost(1, TimeUnit.SECONDS)
            .pollDelay(Duration.ofMillis(50))
            .untilAsserted {
                verify(outboxJpa, atLeast(5)).findUnpublished()
            }

        // Then - should not throw exceptions
        verifyNoInteractions(eventBus)
    }

    @Test
    fun `scheduled task should handle exceptions and continue processing`() {
        // Given
        val event1 = createUnpublishedEvent()
        val event2 = createUnpublishedEvent()

        whenever(outboxJpa.findUnpublished())
            .thenReturn(listOf(event1))  // First run - will fail
            .thenReturn(listOf(event2))  // Second run - will succeed
            .thenReturn(emptyList())     // Subsequent runs

        whenever(eventBus.publish(eq(event1.eventType), any(), any()))
            .thenThrow(RuntimeException("First event fails"))

        // When
        await()
            .atMost(2, TimeUnit.SECONDS)
            .pollDelay(Duration.ofMillis(50))
            .untilAsserted {
                verify(eventBus, atLeastOnce()).publish(eq(event2.eventType), any(), any())
            }

        // Then - both events should have been attempted
        verify(eventBus).publish(eq(event1.eventType), any(), any())
        verify(eventBus).publish(eq(event2.eventType), any(), any())
    }

    @Test
    fun `scheduled task should process batches within configured delay`() {
        // Given
        whenever(outboxJpa.findUnpublished()).thenReturn(emptyList())

        // When - measure time for multiple executions
        val startTime = System.currentTimeMillis()

        await()
            .atMost(1500, TimeUnit.MILLISECONDS)
            .pollDelay(Duration.ofMillis(50))
            .untilAsserted {
                verify(outboxJpa, atLeast(10)).findUnpublished()
            }

        val elapsedTime = System.currentTimeMillis() - startTime

        // Then - should execute roughly every 100ms (with some tolerance)
        // 10 executions with 100ms delay = ~1000ms minimum
        assertTrue(elapsedTime >= 900, "Should take at least 900ms for 10 executions")
        assertTrue(elapsedTime < 1500, "Should complete in less than 1500ms")
    }

    @Test
    fun `should mark event as published after successful publish`() {
        // Given
        val event = createUnpublishedEvent()

        // When
        publisher.publishOne(event)

        // Then
        assertTrue(event.publishedAt != null, "publishedAt should be set")
        assertEquals(1, event.attemptCount, "attemptCount should be incremented")
        assertNull(event.lastError, "lastError should be null")
        verify(outboxJpa).save(event)
    }

    @Test
    fun `should mark event as failed and keep unpublished on exception`() {
        // Given
        val event = createUnpublishedEvent()
        whenever(eventBus.publish(any(), any(), any()))
            .thenThrow(RuntimeException("Publish failed"))

        // When
        publisher.publishOne(event)

        // Then
        assertNull(event.publishedAt, "publishedAt should remain null")
        assertEquals(1, event.attemptCount, "attemptCount should be incremented")
        assertEquals("Publish failed", event.lastError, "lastError should be set")
        verify(outboxJpa).save(event)
    }

    private fun createUnpublishedEvent(): OutboxEventEntity {
        return OutboxEventEntity(
            id = UUID.randomUUID(),
            aggregateType = "TestAggregate",
            aggregateId = UUID.randomUUID().toString(),
            eventType = "test.event.${UUID.randomUUID()}",
            payloadJson = """{"data":"test"}""",
            occurredAt = Instant.now(),
            publishedAt = null,
            attemptCount = 0,
            lastError = null,
        )
    }
}



