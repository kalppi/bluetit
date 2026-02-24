package dev.jarno.bluetit.clip.cliprequests.persistence

import org.junit.jupiter.api.Assertions.*
import dev.jarno.bluetit.clip.cliprequests.ClipStatus
import dev.jarno.bluetit.common.AbstractPostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import java.util.UUID

@SpringBootTest
class ClipRequestJpaRepositoryTest : AbstractPostgresIntegrationTest() {
    @Autowired
    private lateinit var jpa: ClipRequestJpaRepository

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