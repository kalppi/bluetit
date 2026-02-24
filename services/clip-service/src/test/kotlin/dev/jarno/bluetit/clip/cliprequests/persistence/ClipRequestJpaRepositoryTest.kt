package dev.jarno.bluetit.clip.cliprequests.persistence

import org.junit.jupiter.api.Assertions.*
import dev.jarno.bluetit.clip.cliprequests.ClipStatus
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant
import java.util.UUID

@Testcontainers
@DataJpaTest
class ClipRequestJpaRepositoryTest {
    @Autowired
    private lateinit var jpa: ClipRequestJpaRepository

    companion object {
        @Container
        private val postgres = PostgreSQLContainer("postgres:16")

        @JvmStatic
        @DynamicPropertySource
        fun registerProps(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { postgres.jdbcUrl }
            registry.add("spring.datasource.username") { postgres.username }
            registry.add("spring.datasource.password") { postgres.password }
            registry.add("spring.datasource.driver-class-name") { postgres.driverClassName }
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @Test
    fun `entity saves and loads`() {
        val entity = ClipRequestEntity(
            id = UUID.randomUUID().toString(),
            episodeId = "ep-1",
            startSeconds = 1.0,
            endSeconds = 2.5,
            status = ClipStatus.REQUESTED,
            outputUrl = null,
            createdAt = Instant.now(),
        )

        jpa.save(entity)

        val loaded = jpa.findById(entity.id).orElse(null)
        assertNotNull(loaded)
        assertEquals("ep-1", loaded.episodeId)
        assertEquals(ClipStatus.REQUESTED, loaded.status)
    }
}