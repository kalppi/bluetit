package dev.jarno.bluetit.clip.cliprequests.persistence

import dev.jarno.bluetit.clip.cliprequests.ClipRequest
import dev.jarno.bluetit.clip.cliprequests.ClipStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "clip_requests")
class ClipRequestEntity(
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    var id: String,

    @Column(name = "episode_id", nullable = false)
    var episodeId: String,

    @Column(name = "start_seconds", nullable = false)
    var startSeconds: Double,

    @Column(name = "end_seconds", nullable = false)
    var endSeconds: Double,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ClipStatus,

    @Column(name = "output_url", nullable = true)
    var outputUrl: String?,

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant,
) {
    companion object {
        fun fromDomain(domain: ClipRequest): ClipRequestEntity {
            return ClipRequestEntity(
                id = domain.id,
                episodeId = domain.episodeId,
                startSeconds = domain.startSeconds,
                endSeconds = domain.endSeconds,
                status = domain.status,
                outputUrl = domain.outputUrl,
                createdAt = domain.createdAt,
            )
        }
    }

    fun toDomain(): ClipRequest {
        return ClipRequest(
            id = id,
            episodeId = episodeId,
            startSeconds = startSeconds,
            endSeconds = endSeconds,
            status = status,
            outputUrl = outputUrl,
            createdAt = createdAt,
        )
    }
}