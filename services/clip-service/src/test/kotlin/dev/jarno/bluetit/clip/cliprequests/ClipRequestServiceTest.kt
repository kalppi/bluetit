package dev.jarno.bluetit.clip.cliprequests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class ClipRequestServiceTest {
    private val repo = InMemoryClipRequestRepository()
    private val service = ClipRequestService(repo)

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
