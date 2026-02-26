package dev.jarno.bluetit.orchestrator.messaging

import dev.jarno.bluetit.orchestrator.routing.RenderingServiceRouter
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

/**
 * Listens for ClipRequested events and routes them to rendering services
 */
@Component
class ClipRequestedListener(
    private val router: RenderingServiceRouter,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @RabbitListener(queues = ["\${messaging.queue.clip-requested}"])
    fun handleClipRequested(messageJson: String) {
        logger.info("Received clip request message: {}", messageJson)

        try {
            val message = objectMapper.readValue(messageJson, ClipRequestedMessage::class.java)

            logger.info(
                "Processing clip request {} for episode {} ({} - {})",
                message.clipRequestId,
                message.episodeId,
                message.startSeconds,
                message.endSeconds
            )

            router.routeRequest(
                clipRequestId = message.clipRequestId,
                episodeId = message.episodeId,
                startSeconds = message.startSeconds,
                endSeconds = message.endSeconds,
                pipelineId = message.pipelineId,
                pipelineVersion = message.pipelineVersion,
            )

            logger.info("Successfully routed clip request {}", message.clipRequestId)
        } catch (e: Exception) {
            logger.error("Failed to process clip request message", e)
            throw e // Requeue the message for retry
        }
    }
}

