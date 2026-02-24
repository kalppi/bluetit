package dev.jarno.bluetit.clip.cliprequests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest
class ClipRequestServiceTest {
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

    @Autowired
    lateinit var service: ClipRequestService

    @Test
    fun `create stores clip request with REQUESTED status`() {
        val created = service.create("ep-1", 1.0, 2.5)

        assertNotNull(created.id)
        assertEquals("ep-1", created.episodeId)
        assertEquals(ClipStatus.REQUESTED, created.status)

        val loaded = service.get(created.id)
        assertEquals(created.id, loaded.id)
    }

    @Test
    fun `create throws for endSeconds less or equal to startSeconds`() {
        assertThrows(BadRequestException::class.java) {
            service.create("ep-1", 10.0, 10.0)
        }
    }

    @Test
    fun `get throws NotFound for missing id`() {
        assertThrows(NotFoundException::class.java) {
            service.get("missing")
        }
    }
}
