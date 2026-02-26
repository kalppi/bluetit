package dev.jarno.bluetit.clip

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(
    scanBasePackages = [
        "dev.jarno.bluetit.clip",
        "dev.jarno.bluetit.outbox"
    ]
)
@EnableJpaRepositories(
    basePackages = [
        "dev.jarno.bluetit.clip",
        "dev.jarno.bluetit.outbox"
    ]
)
@EntityScan(basePackages = ["dev.jarno.bluetit.clip", "dev.jarno.bluetit.outbox"])
class ClipServiceApplication

fun main(args: Array<String>) {
    runApplication<ClipServiceApplication>(*args)
}
