package dev.jarno.bluetit.orchestrator.routing

/**
 * Interface for routing clip requests to rendering services
 */
interface RenderingServiceRouter {
    /**
     * Route a clip request to an available rendering service
     * @param clipRequestId The unique identifier for the clip request
     * @param episodeId The episode to render
     * @param startSeconds Start time of the clip
     * @param endSeconds End time of the clip
     * @param pipelineId The rendering pipeline to use
     * @param pipelineVersion Version of the rendering pipeline
     * @return The URL of the rendering service that accepted the request
     */
    fun routeRequest(
        clipRequestId: String,
        episodeId: String,
        startSeconds: Double,
        endSeconds: Double,
        pipelineId: String,
        pipelineVersion: String,
    ): String
}

