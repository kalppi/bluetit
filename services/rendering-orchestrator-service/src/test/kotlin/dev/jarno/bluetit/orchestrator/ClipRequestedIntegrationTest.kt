package dev.jarno.bluetit.orchestrator

import dev.jarno.bluetit.orchestrator.messaging.ClipRequestedMessage
import dev.jarno.bluetit.orchestrator.routing.RenderingServiceRouter
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.reset
import org.mockito.kotlin.timeout
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import tools.jackson.databind.ObjectMapper
import java.util.concurrent.TimeUnit

@SpringBootTest
@Testcontainers
@TestPropertySource(
    properties = [
        "spring.datasource.url=",
        "spring.datasource.driver-class-name=",
        "spring.jpa.database-platform="
    ]
)
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
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var router: RenderingServiceRouter

    @BeforeEach
    fun setUp() {
        reset(router)
        whenever(router.routeRequest(any(), any(), any(), any(), any(), any()))
            .thenReturn("http://service1:9000")
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

        val messageJson = objectMapper.writeValueAsString(message)

        // When
        rabbitTemplate.convertAndSend(
            "events.exchange",
            "ClipRequested",
            messageJson
        )

        // Then
        await()
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted {
                verify(router, timeout(5000)).routeRequest(
                    clipRequestId = eq("clip-123"),
                    episodeId = eq("episode-456"),
                    startSeconds = eq(10.5),
                    endSeconds = eq(20.5),
                    pipelineId = eq("gif"),
                    pipelineVersion = eq("v1")
                )
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

        // When
        messages.forEach { message ->
            val messageJson = objectMapper.writeValueAsString(message)
            rabbitTemplate.convertAndSend(
                "events.exchange",
                "ClipRequested",
                messageJson
            )
        }

        // Then
        await()
            .atMost(10, TimeUnit.SECONDS)
            .untilAsserted {
                verify(router, timeout(5000)).routeRequest(
                    clipRequestId = eq("clip-1"),
                    episodeId = eq("episode-1"),
                    startSeconds = eq(1.0),
                    endSeconds = eq(5.0),
                    pipelineId = eq("gif"),
                    pipelineVersion = eq("v1")
                )
                verify(router, timeout(5000)).routeRequest(
                    clipRequestId = eq("clip-2"),
                    episodeId = eq("episode-2"),
                    startSeconds = eq(2.0),
                    endSeconds = eq(6.0),
                    pipelineId = eq("gif"),
                    pipelineVersion = eq("v1")
                )
                verify(router, timeout(5000)).routeRequest(
                    clipRequestId = eq("clip-3"),
                    episodeId = eq("episode-3"),
                    startSeconds = eq(3.0),
                    endSeconds = eq(7.0),
                    pipelineId = eq("gif"),
                    pipelineVersion = eq("v1")
                )
            }
    }
}

