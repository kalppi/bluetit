package dev.jarno.bluetit.common

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.RabbitMQContainer

abstract class AbstractRabbitMqIntegrationTest {
    companion object {
        @JvmField
        val rabbitmq = RabbitMQContainer("rabbitmq:3-management").apply { start() }

        init {
            Runtime.getRuntime().addShutdownHook(Thread {
                rabbitmq.stop()
            })
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerRabbitMqProps(registry: DynamicPropertyRegistry) {
            registry.add("spring.rabbitmq.host") { rabbitmq.host }
            registry.add("spring.rabbitmq.port") { rabbitmq.getMappedPort(5672) }
            registry.add("spring.rabbitmq.username") { rabbitmq.adminUsername }
            registry.add("spring.rabbitmq.password") { rabbitmq.adminPassword }
        }
    }
}

