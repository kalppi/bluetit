package dev.jarno.bluetit.clip.cliprequests

import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class ClipRequestService(
    private val repo: ClipRequestRepository,
) {

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

        return clip
    }

    fun get(id: String): ClipRequest {
        return repo.findById(id) ?: throw NotFoundException("ClipRequest not found")
    }

    fun getAll(): List<ClipRequest> {
        return repo.findAll()
    }
}