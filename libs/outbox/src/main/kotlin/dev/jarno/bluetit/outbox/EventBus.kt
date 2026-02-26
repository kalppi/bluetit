package dev.jarno.bluetit.outbox

interface EventBus {
    fun publish(eventType: String, payloadJson: String, aggregateId: String)
}

