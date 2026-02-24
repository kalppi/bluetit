package dev.jarno.bluetit.clip.cliprequests

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.empty
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class ClipRequestControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `POST creates clip request and returns 202`() {
        mockMvc.post("/api/v1/clip-requests") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"episodeId":"ep-1","startSeconds":12.3,"endSeconds":18.0}"""
        }
            .andExpect {
                status { isAccepted() }
                jsonPath("$.clipRequestId") { exists() }
                jsonPath("$.status") { value(`is`("REQUESTED")) }
            }
    }

    @Test
    fun `POST rejects invalid timespan`() {
        mockMvc.post("/api/v1/clip-requests") {
            contentType = MediaType.APPLICATION_JSON
            content = """{"episodeId":"ep-1","startSeconds":10.0,"endSeconds":10.0}"""
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `GET returns 404 for unknown id`() {
        mockMvc.get("/api/v1/clip-requests/does-not-exist")
            .andExpect {
                status { isNotFound() }
            }
    }

    @Test
    fun `GET returns all`() {
        mockMvc.get("/api/v1/clip-requests/all")
            .andExpect {
                status { isOk() }
                jsonPath("$") { isArray() }
                jsonPath("$") { value(empty<Any>()) }
            }
    }
}