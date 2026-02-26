package dev.jarno.bluetit.clip

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan(
    basePackages = [
        "dev.jarno.bluetit.clip",
        "dev.jarno.bluetit.outbox"
    ]
)
@EnableJpaRepositories(basePackages = [
    "dev.jarno.bluetit.clip"
])
class ClipServiceApplication

fun main(args: Array<String>) {
    runApplication<ClipServiceApplication>(*args)
}
