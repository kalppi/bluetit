package dev.jarno.bluetit.bff.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/health")
class HealthController {

	@GetMapping
	fun health(): ResponseEntity<Map<String, String>> {
		return ResponseEntity.ok(mapOf("status" to "UP"))
	}
}

