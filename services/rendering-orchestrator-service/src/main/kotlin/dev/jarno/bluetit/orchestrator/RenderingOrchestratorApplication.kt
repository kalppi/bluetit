package dev.jarno.bluetit.orchestrator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(
    basePackages = [
        "dev.jarno.bluetit.orchestrator",
        "dev.jarno.bluetit.outbox"
    ]
)
@EnableJpaRepositories(basePackages = [
    "dev.jarno.bluetit.orchestrator"
])
class RenderingOrchestratorApplication

fun main(args: Array<String>) {
    runApplication<RenderingOrchestratorApplication>(*args)
}

