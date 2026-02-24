package dev.jarno.bluetit.clip.cliprequests

import dev.jarno.bluetit.clip.cliprequests.dto.ClipRequestDto
import dev.jarno.bluetit.clip.cliprequests.dto.CreateClipRequestDto
import dev.jarno.bluetit.clip.cliprequests.dto.CreateClipRequestResponseDto
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/clip-requests")
class ClipRequestController(
    private val service: ClipRequestService,
) {

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun create(@Valid @RequestBody req: CreateClipRequestDto): CreateClipRequestResponseDto {
        val created = service.create(req.episodeId, req.startSeconds, req.endSeconds)

        return CreateClipRequestResponseDto(
            clipRequestId = created.id,
            status = created.status,
        )
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: String): ClipRequestDto {
        val found = service.get(id)

        return ClipRequestDto(
            clipRequestId = found.id,
            episodeId = found.episodeId,
            startSeconds = found.startSeconds,
            endSeconds = found.endSeconds,
            status = found.status,
            outputUrl = found.outputUrl,
            createdAt = found.createdAt,
        )
    }

    @GetMapping("/all")
    fun getAll(): List<ClipRequestDto> {
        return service.getAll().map {
            ClipRequestDto(
                clipRequestId = it.id,
                episodeId = it.episodeId,
                startSeconds = it.startSeconds,
                endSeconds = it.endSeconds,
                status = it.status,
                outputUrl = it.outputUrl,
                createdAt = it.createdAt,
            )
        }
    }
}