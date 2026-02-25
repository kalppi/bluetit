package dev.jarno.bluetit.clip.cliprequests

import dev.jarno.bluetit.clip.outbox.OutboxWriter
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class ClipRequestService(
    private val repo: ClipRequestRepository,
    private val outboxWriter: OutboxWriter,
) {

    @Transactional
    fun create(episodeId: String, startSeconds: Double, endSeconds: Double): ClipRequest {
        if (endSeconds <= startSeconds) {
            throw BadRequestException("endSeconds must be > startSeconds")
        }
        if (startSeconds < 0.0) {
            throw BadRequestException("startSeconds must be >= 0")
        }

        val clip = ClipRequest(
            id = UUID.randomUUID().toString(),
            episodeId = episodeId,
            startSeconds = startSeconds,
            endSeconds = endSeconds,
            status = ClipStatus.REQUESTED,
            outputUrl = null,
            createdAt = Instant.now(),
        )

        repo.save(clip)

        outboxWriter.add(
            aggregateType = "ClipRequest",
            aggregateId = clip.id,
            eventType = "ClipRequested",
            payload = ClipRequestedPayload(
                clipRequestId = clip.id,
                episodeId = clip.episodeId,
                startSeconds = clip.startSeconds,
                endSeconds = clip.endSeconds,
                pipelineId = "gif",
                pipelineVersion = "v1",
            ),
        )

        return clip
    }

    fun get(id: String): ClipRequest {
        return repo.findById(id) ?: throw NotFoundException("ClipRequest not found")
    }

    fun getAll(): List<ClipRequest> {
        return repo.findAll()
    }
}