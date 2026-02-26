package dev.jarno.bluetit.orchestrator.routing

data class RenderingRequest(
    val clipRequestId: String,
    val episodeId: String,
    val startSeconds: Double,
    val endSeconds: Double,
    val pipelineId: String,
    val pipelineVersion: String,
)
