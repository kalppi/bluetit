package dev.jarno.bluetit.clip.cliprequests.dto

import dev.jarno.bluetit.clip.cliprequests.ClipStatus

data class CreateClipRequestResponseDto(
    val clipRequestId: String,
    val status: ClipStatus,
)