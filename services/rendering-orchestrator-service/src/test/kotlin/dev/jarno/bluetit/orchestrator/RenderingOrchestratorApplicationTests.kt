package dev.jarno.bluetit.orchestrator

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestPropertySource(
	properties = [
		"spring.datasource.url=",
		"spring.datasource.driver-class-name=",
		"spring.jpa.database-platform="
	]
)
class RenderingOrchestratorApplicationTests {

	@Test
	fun contextLoads() {
	}

}

