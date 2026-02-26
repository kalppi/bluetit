package dev.jarno.bluetit.orchestrator.messaging

import dev.jarno.bluetit.outbox.OutboxPayload

data class RenderingRequestedPayload(
    val clipRequestId: String,
    val episodeId: String,
    val startSeconds: Double,
    val endSeconds: Double,
    val pipelineId: String,
    val pipelineVersion: String,
    val renderingServiceUrl: String,
) : OutboxPayload()

