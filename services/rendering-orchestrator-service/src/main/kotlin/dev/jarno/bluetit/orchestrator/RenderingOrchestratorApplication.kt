package dev.jarno.bluetit.orchestrator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RenderingOrchestratorApplication

fun main(args: Array<String>) {
	runApplication<RenderingOrchestratorApplication>(*args)
}

