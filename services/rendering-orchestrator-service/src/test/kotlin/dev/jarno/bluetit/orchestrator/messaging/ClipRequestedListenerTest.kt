package dev.jarno.bluetit.orchestrator.messaging

import dev.jarno.bluetit.orchestrator.routing.RenderingServiceRouter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class ClipRequestedListenerTest {

    private val router = mock<RenderingServiceRouter>()
    private val listener = ClipRequestedListener(router)

    @Test
    fun `should parse message and route to rendering service`() {
        // Given
        val message = ClipRequestedMessage(
            clipRequestId = "clip-123",
            episodeId = "episode-456",
            startSeconds = 10.5,
            endSeconds = 15.5,
            pipelineId = "gif",
            pipelineVersion = "v1"
        )

        // When
        listener.handleClipRequested(message)

        // Then
        verify(router).routeRequest(
            clipRequestId = eq("clip-123"),
            episodeId = eq("episode-456"),
            startSeconds = eq(10.5),
            endSeconds = eq(15.5),
            pipelineId = eq("gif"),
            pipelineVersion = eq("v1")
        )
    }

    @Test
    fun `should rethrow exception when routing fails`() {
        // Given
        val message = ClipRequestedMessage(
            clipRequestId = "clip-123",
            episodeId = "episode-456",
            startSeconds = 10.5,
            endSeconds = 15.5,
            pipelineId = "gif",
            pipelineVersion = "v1"
        )

        whenever(router.routeRequest(any(), any(), any(), any(), any(), any()))
            .thenThrow(RuntimeException("No services available"))

        // When/Then
        assertThrows<RuntimeException> {
            listener.handleClipRequested(message)
        }
    }
}

