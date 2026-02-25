package dev.jarno.bluetit.common

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer

abstract class AbstractPostgresIntegrationTest {
    companion object {
        @JvmField
        val postgres = PostgreSQLContainer<Nothing>("postgres:16").apply { start() }

        init {
            Runtime.getRuntime().addShutdownHook(Thread {
                postgres.stop()
            })
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerProps(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
            registry.add("spring.datasource.driver-class-name") { postgres.driverClassName }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create" }
        }
    }
}
