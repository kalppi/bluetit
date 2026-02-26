package dev.jarno.bluetit.outbox

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@ComponentScan(basePackages = ["dev.jarno.bluetit.outbox"])
@EnableJpaRepositories(basePackages = ["dev.jarno.bluetit.outbox"])
class OutboxAutoConfiguration

