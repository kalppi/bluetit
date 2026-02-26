package dev.jarno.bluetit.orchestrator.routing

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.concurrent.atomic.AtomicInteger

/**
 * Round-robin load balancer for distributing clip requests to rendering services
 */
@Component
class RoundRobinRenderingServiceRouter(
    private val renderingServicePool: RenderingServicePool,
    private val restTemplate: RestTemplate,
) : RenderingServiceRouter {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val counter = AtomicInteger(0)

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
            // Send the rendering request
            val request = RenderingRequest(
                clipRequestId = clipRequestId,
                episodeId = episodeId,
                startSeconds = startSeconds,
                endSeconds = endSeconds,
                pipelineId = pipelineId,
                pipelineVersion = pipelineVersion,
            )

            restTemplate.postForEntity(
                "$selectedService/api/v1/render",
                request,
                String::class.java
            )

            logger.info("Successfully routed clip request {} to {}", clipRequestId, selectedService)
            return selectedService
        } catch (e: Exception) {
            logger.error("Failed to route clip request {} to {}", clipRequestId, selectedService, e)
            throw RenderingServiceRoutingException("Failed to route request to $selectedService", e)
        }
    }
}



