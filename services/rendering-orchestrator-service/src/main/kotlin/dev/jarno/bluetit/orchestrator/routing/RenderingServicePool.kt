package dev.jarno.bluetit.orchestrator.routing

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * Manages the pool of available rendering services
 */
@Component
class RenderingServicePool(
    private val properties: RenderingServicePoolProperties,
) {

    /**
     * Get list of currently available rendering service URLs
     */
    fun getAvailableServices(): List<String> {
        return properties.services.filter { it.isNotBlank() }
    }
}

@Component
@ConfigurationProperties(prefix = "rendering.service.pool")
data class RenderingServicePoolProperties(
    var services: List<String> = emptyList(),
)

