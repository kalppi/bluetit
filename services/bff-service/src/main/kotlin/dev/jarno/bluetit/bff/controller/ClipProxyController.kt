package dev.jarno.bluetit.bff.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate

@RestController
@RequestMapping("/api/clips")
class ClipProxyController(
    private val restTemplate: RestTemplate
) {

    @Value("\${services.clip-service.url:http://localhost:8081}")
    private lateinit var clipServiceUrl: String

    @GetMapping
    fun getAllClips(): ResponseEntity<Any> {
        return restTemplate.exchange(
            "$clipServiceUrl/api/clips",
            HttpMethod.GET,
            null,
            Any::class.java
        )
    }

    @GetMapping("/{id}")
    fun getClipById(@PathVariable id: String): ResponseEntity<Any> {
        return restTemplate.exchange(
            "$clipServiceUrl/api/clips/$id",
            HttpMethod.GET,
            null,
            Any::class.java
        )
    }

    @PostMapping
    fun createClip(@RequestBody clip: Any): ResponseEntity<Any> {
        val request = HttpEntity(clip)
        return restTemplate.exchange(
            "$clipServiceUrl/api/clips",
            HttpMethod.POST,
            request,
            Any::class.java
        )
    }

    @PutMapping("/{id}")
    fun updateClip(@PathVariable id: String, @RequestBody clip: Any): ResponseEntity<Any> {
        val request = HttpEntity(clip)
        return restTemplate.exchange(
            "$clipServiceUrl/api/clips/$id",
            HttpMethod.PUT,
            request,
            Any::class.java
        )
    }

    @DeleteMapping("/{id}")
    fun deleteClip(@PathVariable id: String): ResponseEntity<Any> {
        return restTemplate.exchange(
            "$clipServiceUrl/api/clips/$id",
            HttpMethod.DELETE,
            null,
            Any::class.java
        )
    }
}

