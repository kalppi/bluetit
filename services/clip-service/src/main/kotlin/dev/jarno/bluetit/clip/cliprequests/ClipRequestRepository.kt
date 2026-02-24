package dev.jarno.bluetit.clip.cliprequests

import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap

interface ClipRequestRepository {
    fun save(clip: ClipRequest)
    fun findById(id: String): ClipRequest?
    fun findAll(): List<ClipRequest>
}

@Repository
class InMemoryClipRequestRepository : ClipRequestRepository {
    private val store = ConcurrentHashMap<String, ClipRequest>()

    override fun save(clip: ClipRequest) {
        store[clip.id] = clip
    }

    override fun findById(id: String): ClipRequest? {
        return store[id]
    }

    override fun findAll(): List<ClipRequest> {
        return store.values.toList()
    }
}