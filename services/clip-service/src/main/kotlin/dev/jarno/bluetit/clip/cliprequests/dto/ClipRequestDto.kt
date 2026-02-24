package dev.jarno.bluetit.clip.cliprequests.dto

import dev.jarno.bluetit.clip.cliprequests.ClipStatus
import java.time.Instant

data class ClipRequestDto(
    val clipRequestId: String,
    val episodeId: String,
    val startSeconds: Double,
    val endSeconds: Double,
    val status: ClipStatus,
    val outputUrl: String?,
    val createdAt: Instant,
)