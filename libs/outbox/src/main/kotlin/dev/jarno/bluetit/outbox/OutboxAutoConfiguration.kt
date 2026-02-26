package dev.jarno.bluetit.outbox

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["dev.jarno.bluetit.outbox"])
class OutboxAutoConfiguration
