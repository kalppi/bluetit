package dev.jarno.bluetit.clip

import dev.jarno.bluetit.clip.cliprequests.messaging.ClipRequestedPayload
import dev.jarno.bluetit.common.AbstractPostgresIntegrationTest
import dev.jarno.bluetit.outbox.EventBus
import dev.jarno.bluetit.outbox.OutboxEventJpaRepository
import dev.jarno.bluetit.outbox.OutboxPublisher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doAnswer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.mockito.kotlin.any
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.bean.override.mockito.MockitoBean
import tools.jackson.databind.ObjectMapper

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(
    properties = [
        "app.scheduling.enabled=false"
    ]
)
class OutboxIntegrationTest : AbstractPostgresIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var outboxJpa: OutboxEventJpaRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var outboxPublisher: OutboxPublisher

    @MockitoBean
    private lateinit var eventBus: EventBus

    @BeforeEach
    fun setUp() {
        outboxJpa.deleteAll()
    }

    private fun runOutboxSchedulerOnce() {
        outboxPublisher.publishBatch()
    }

    @Test
    fun `should create clip request and write event to outbox`() {
        // When create a clip request via HTTP POST
        mockMvc.post("/api/v1/clip-requests") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = """
               {
                   "episodeId": "episode-123",
                   "startSeconds": 10.5,
                   "endSeconds": 25.0
               }
           """
        }.andExpect {
            status { isAccepted() }
        }

        // Then verify event was written to outbox
        val events = outboxJpa.findAll()
        assertEquals(1, events.size, "Should have 1 event in outbox")

        val event = events.first()
        assertEquals("ClipRequest", event.aggregateType)
        assertEquals("ClipRequested", event.eventType)
        assertNull(event.publishedAt, "Event should not be published yet")
        assertEquals(0, event.attemptCount, "Should have 0 attempts initially")
        assertNull(event.lastError)

        // Verify the payload contains correct data
        val payload = objectMapper.readValue(event.payloadJson, ClipRequestedPayload::class.java)
        assertEquals("episode-123", payload.episodeId)
        assertEquals(10.5, payload.startSeconds)
        assertEquals(25.0, payload.endSeconds)
        assertEquals("gif", payload.pipelineId)
        assertEquals("v1", payload.pipelineVersion)
    }

    @Test
    fun `should publish event to rabbitmq via scheduler`() {
        // Given create a clip request
        mockMvc.post("/api/v1/clip-requests") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = """
               {
                   "episodeId": "episode-456",
                   "startSeconds": 5.0,
                   "endSeconds": 15.0
               }
           """
        }

        // When trigger scheduler manually
        runOutboxSchedulerOnce()

        // Then verify event is marked as published
        val events = outboxJpa.findAll()
        assertEquals(1, events.size)

        val event = events.first()
        assertNotNull(event.publishedAt, "Event should be marked as published")
        assertEquals(1, event.attemptCount, "Should have 1 attempt")
        assertNull(event.lastError, "Should have no error")
    }

    @Test
    fun `should verify correct event data sent to rabbitmq`() {
        // Given create a clip request
        val episodeId = "episode-789"
        val startSeconds = 20.5
        val endSeconds = 45.75

        mockMvc.post("/api/v1/clip-requests") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = """
               {
                   "episodeId": "$episodeId",
                   "startSeconds": $startSeconds,
                   "endSeconds": $endSeconds
               }
           """
        }

        // When trigger scheduler manually
        runOutboxSchedulerOnce()

        // Then get the outbox event and verify it matches what was sent
        val events = outboxJpa.findAll()
        val event = events.first()

        val payload = objectMapper.readValue(event.payloadJson, ClipRequestedPayload::class.java)
        assertEquals(episodeId, payload.episodeId)
        assertEquals(startSeconds, payload.startSeconds)
        assertEquals(endSeconds, payload.endSeconds)
        assertEquals("gif", payload.pipelineId)
        assertEquals("v1", payload.pipelineVersion)

        // Verify the event was sent to the correct event type
        assertEquals("ClipRequested", event.eventType)
    }

    @Test
    fun `should handle rabbitmq publish failure and retry`() {
        // Given configure event bus to fail first, then succeed
        var callCount = 0
        doAnswer {
            callCount++
            if (callCount == 1) {
                throw RuntimeException("RabbitMQ connection failed")
            }
        }.`when`(eventBus).publish(any(), any(), any())

        // When create a clip request
        mockMvc.post("/api/v1/clip-requests") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = """
               {
                   "episodeId": "episode-retry",
                   "startSeconds": 1.0,
                   "endSeconds": 2.0
               }
           """
        }

        // Trigger first attempt (which fails)
        runOutboxSchedulerOnce()

        // Then verify first attempt recorded the error
        var events = outboxJpa.findAll()
        var event = events.first()
        assertEquals(1, event.attemptCount, "Should have attempted once")
        assertEquals("RabbitMQ connection failed", event.lastError)
        assertNull(event.publishedAt, "Should not be published after failure")

        // When trigger retry
        runOutboxSchedulerOnce()

        // Then verify event is now published
        event = outboxJpa.findById(event.id).get()
        assertEquals(2, event.attemptCount, "Should have attempted twice")
        assertNotNull(event.publishedAt, "Should be published after retry")
        assertNull(event.lastError, "Error should be cleared after success")
    }

    @Test
    fun `should handle multiple concurrent clip requests`() {
        // When create multiple clip requests
        repeat(5) { i ->
            mockMvc.post("/api/v1/clip-requests") {
                contentType = org.springframework.http.MediaType.APPLICATION_JSON
                content = """
                   {
                       "episodeId": "episode-$i",
                       "startSeconds": ${i.toDouble()},
                       "endSeconds": ${(i + 10).toDouble()}
                   }
               """
            }
        }

        // Then verify all events are in outbox
        assertEquals(5, outboxJpa.findAll().size, "Should have 5 events in outbox")

        // When trigger scheduler manually
        runOutboxSchedulerOnce()

        // Then verify all events are published
        val allEvents = outboxJpa.findAll()
        allEvents.forEach { event ->
            assertNotNull(event.publishedAt, "Event ${event.id} should be published")
            assertEquals(1, event.attemptCount)
            assertNull(event.lastError)
        }
    }

    @Test
    fun `should not publish events with invalid request`() {
        // When create an invalid clip request (endSeconds <= startSeconds)
        mockMvc.post("/api/v1/clip-requests") {
            contentType = org.springframework.http.MediaType.APPLICATION_JSON
            content = """
               {
                   "episodeId": "episode-invalid",
                   "startSeconds": 25.0,
                   "endSeconds": 10.0
               }
           """
        }.andExpect {
            status { isBadRequest() }
        }

        // Then verify no event was created
        assertEquals(0, outboxJpa.findAll().size, "Should have no events for invalid request")
    }
}
