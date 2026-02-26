package dev.jarno.bluetit.orchestrator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    scanBasePackages = [
        "dev.jarno.bluetit.orchestrator",
        "dev.jarno.bluetit.outbox"
    ]
)
@EnableJpaRepositories(
    basePackages = [
        "dev.jarno.bluetit.orchestrator",
        "dev.jarno.bluetit.outbox"
    ]
)
@EntityScan(basePackages = ["dev.jarno.bluetit.orchestrator", "dev.jarno.bluetit.outbox"])
class RenderingOrchestratorApplication

fun main(args: Array<String>) {
    runApplication<RenderingOrchestratorApplication>(*args)
}

