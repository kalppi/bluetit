package dev.jarno.bluetit.common

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.RabbitMQContainer

/**
 * Base class for integration tests that require both PostgreSQL and RabbitMQ.
 * Uses testcontainers to provide isolated database and message broker instances.
 */
abstract class AbstractFullIntegrationTest {
    companion object {
        @JvmField
        val postgres = PostgreSQLContainer<Nothing>("postgres:16").apply { start() }

        @JvmField
        val rabbitmq = RabbitMQContainer("rabbitmq:3-management").apply { start() }

        init {
            Runtime.getRuntime().addShutdownHook(Thread {
                postgres.stop()
                rabbitmq.stop()
            })
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProps(registry: DynamicPropertyRegistry) {
            // PostgreSQL properties
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
            registry.add("spring.datasource.driver-class-name") { postgres.driverClassName }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create" }

            // RabbitMQ properties
            registry.add("spring.rabbitmq.host") { rabbitmq.host }
            registry.add("spring.rabbitmq.port") { rabbitmq.getMappedPort(5672) }
            registry.add("spring.rabbitmq.username") { rabbitmq.adminUsername }
            registry.add("spring.rabbitmq.password") { rabbitmq.adminPassword }
        }
    }
}

