package dev.jarno.bluetit.orchestrator

import dev.jarno.bluetit.orchestrator.messaging.ClipRequestedMessage
import dev.jarno.bluetit.outbox.OutboxEventJpaRepository
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@Testcontainers
class ClipRequestedIntegrationTest {

    companion object {
        @Container
        val rabbitMqContainer = RabbitMQContainer("rabbitmq:3-management")
            .withExposedPorts(5672, 15672)

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.rabbitmq.host") { rabbitMqContainer.host }
            registry.add("spring.rabbitmq.port") { rabbitMqContainer.getMappedPort(5672) }
            registry.add("spring.rabbitmq.username") { "guest" }
            registry.add("spring.rabbitmq.password") { "guest" }
        }
    }

    @Autowired
    private lateinit var rabbitTemplate: RabbitTemplate

    @Autowired
    private lateinit var outboxRepository: OutboxEventJpaRepository

    @BeforeEach
    fun setUp() {
        outboxRepository.deleteAll()
    }

    @Test
    fun `should receive clip requested message and route to rendering service`() {
        // Given
        val message = ClipRequestedMessage(
            clipRequestId = "clip-123",
            episodeId = "episode-456",
            startSeconds = 10.5,
            endSeconds = 20.5,
            pipelineId = "gif",
            pipelineVersion = "v1"
        )

        // When - send the object directly, let RabbitTemplate serialize it
        rabbitTemplate.convertAndSend(
            "events.exchange",
            "ClipRequested",
            message
        )

        // Then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted {
                val events = outboxRepository.findAll()
                assertTrue(events.isNotEmpty(), "Outbox should contain at least one event")
                val event = events.first()
                assertEquals("ClipRequest", event.aggregateType)
                assertEquals("clip-123", event.aggregateId)
                assertEquals("RenderingRequested", event.eventType)
            }
    }

    @Test
    fun `should handle multiple clip requests in sequence`() {
        // Given
        val messages = listOf(
            ClipRequestedMessage("clip-1", "episode-1", 1.0, 5.0, "gif", "v1"),
            ClipRequestedMessage("clip-2", "episode-2", 2.0, 6.0, "gif", "v1"),
            ClipRequestedMessage("clip-3", "episode-3", 3.0, 7.0, "gif", "v1")
        )

        // When - send objects directly, let RabbitTemplate serialize them
        messages.forEach { message ->
            rabbitTemplate.convertAndSend(
                "events.exchange",
                "ClipRequested",
                message
            )
        }

        // Then
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted {
                val events = outboxRepository.findAll()
                assertEquals(3, events.size, "Outbox should contain three events")

                val sorted = events.sortedBy { it.aggregateId }

                assertEquals("clip-1", sorted[0].aggregateId)
                assertEquals("ClipRequest", sorted[0].aggregateType)
                assertEquals("RenderingRequested", sorted[0].eventType)

                assertEquals("clip-2", sorted[1].aggregateId)
                assertEquals("ClipRequest", sorted[1].aggregateType)
                assertEquals("RenderingRequested", sorted[1].eventType)

                assertEquals("clip-3", sorted[2].aggregateId)
                assertEquals("ClipRequest", sorted[2].aggregateType)
                assertEquals("RenderingRequested", sorted[2].eventType)
            }
    }
}
