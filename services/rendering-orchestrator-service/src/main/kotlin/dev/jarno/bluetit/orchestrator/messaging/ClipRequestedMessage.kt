package dev.jarno.bluetit.orchestrator.messaging

data class ClipRequestedMessage(
    val clipRequestId: String,
    val episodeId: String,
    val startSeconds: Double,
    val endSeconds: Double,
    val pipelineId: String,
    val pipelineVersion: String,
)

