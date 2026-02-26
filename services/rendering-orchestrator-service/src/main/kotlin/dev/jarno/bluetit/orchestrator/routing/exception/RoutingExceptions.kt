package dev.jarno.bluetit.orchestrator.routing.exception

class NoAvailableRenderingServiceException(message: String) : RuntimeException(message)

class RenderingServiceRoutingException(message: String, cause: Throwable) : RuntimeException(message, cause)
