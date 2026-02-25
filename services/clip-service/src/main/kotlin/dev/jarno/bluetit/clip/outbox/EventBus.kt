package dev.jarno.bluetit.clip.outbox

interface EventBus {
    fun publish(eventType: String, payloadJson: String, aggregateId: String)
}