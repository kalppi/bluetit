package dev.jarno.bluetit.clip.cliprequests.dto

import jakarta.validation.constraints.*

data class CreateClipRequestDto(
    @field:NotBlank
    val episodeId: String,

    @field:PositiveOrZero
    val startSeconds: Double,

    @field:PositiveOrZero
    val endSeconds: Double,
)