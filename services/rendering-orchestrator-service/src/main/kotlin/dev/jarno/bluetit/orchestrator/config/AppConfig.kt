package dev.jarno.bluetit.orchestrator.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties
class AppConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
}

