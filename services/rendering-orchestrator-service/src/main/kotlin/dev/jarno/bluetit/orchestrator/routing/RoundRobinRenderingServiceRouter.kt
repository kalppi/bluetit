package dev.jarno.bluetit.orchestrator.routing

import dev.jarno.bluetit.orchestrator.messaging.RenderingRequestedPayload
import dev.jarno.bluetit.orchestrator.routing.exception.NoAvailableRenderingServiceException
import dev.jarno.bluetit.orchestrator.routing.exception.RenderingServiceRoutingException
import dev.jarno.bluetit.outbox.OutboxWriter
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

/**
 * Round-robin load balancer for distributing clip requests to rendering services
 */
@Component
class RoundRobinRenderingServiceRouter(
    private val renderingServicePool: RenderingServicePool,
    private val outboxWriter: OutboxWriter,
) : RenderingServiceRouter {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val counter = AtomicInteger(0)

    @Transactional
    override fun routeRequest(
        clipRequestId: String,
        episodeId: String,
        startSeconds: Double,
        endSeconds: Double,
        pipelineId: String,
        pipelineVersion: String,
    ): String {
        val services = renderingServicePool.getAvailableServices()

        if (services.isEmpty()) {
            throw NoAvailableRenderingServiceException("No rendering services available")
        }

        // Round-robin selection
        val index = counter.getAndIncrement() % services.size
        val selectedService = services[index]

        logger.info("Routing clip request {} to rendering service at {}", clipRequestId, selectedService)

        try {
            // Send the rendering request via outbox pattern
            outboxWriter.add(
                aggregateType = "ClipRequest",
                aggregateId = clipRequestId,
                eventType = "RenderingRequested",
                payload = RenderingRequestedPayload(
                    clipRequestId = clipRequestId,
                    episodeId = episodeId,
                    startSeconds = startSeconds,
                    endSeconds = endSeconds,
                    pipelineId = pipelineId,
                    pipelineVersion = pipelineVersion,
                    renderingServiceUrl = selectedService,
                ),
            )

            logger.info("Successfully routed clip request {} to {}", clipRequestId, selectedService)
            return selectedService
        } catch (e: Exception) {
            logger.error("Failed to route clip request {} to {}", clipRequestId, selectedService, e)
            throw RenderingServiceRoutingException("Failed to route request to $selectedService", e)
        }
    }
}

