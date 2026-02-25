package dev.jarno.bluetit.clip.cliprequests

import dev.jarno.bluetit.clip.outbox.OutboxEventJpaRepository
import dev.jarno.bluetit.common.AbstractPostgresIntegrationTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ClipRequestServiceTest : AbstractPostgresIntegrationTest() {

    @Autowired
    lateinit var service: ClipRequestService

    @Autowired
    lateinit var outboxRepo: OutboxEventJpaRepository

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
        assertThrows<BadRequestException> {
            service.create("ep-1", 10.0, 10.0)
        }
    }

    @Test
    fun `get throws NotFound for missing id`() {
        assertThrows<NotFoundException> {
            service.get("missing")
        }
    }

    @Test
    fun `create writes outbox event`() {
        outboxRepo.deleteAll()

        val created = service.create("ep-1", 1.0, 2.5)

        val events = outboxRepo.findAll()
        assertEquals(1, events.size)
        assertEquals("ClipRequested", events.first().eventType)
        assertTrue(events.first().payloadJson.contains(created.id))
    }
}
