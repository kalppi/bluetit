package dev.jarno.bluetit.clip.cliprequests.messaging
import dev.jarno.bluetit.outbox.OutboxPayload
data class ClipRequestedPayload(
    val clipRequestId: String,
    val episodeId: String,
    val startSeconds: Double,
    val endSeconds: Double,
    val pipelineId: String,
    val pipelineVersion: String,
) : OutboxPayload()
