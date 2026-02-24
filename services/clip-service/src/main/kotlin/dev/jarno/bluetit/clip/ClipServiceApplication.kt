package dev.jarno.bluetit.clip

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ClipServiceApplication

fun main(args: Array<String>) {
	runApplication<ClipServiceApplication>(*args)
}
