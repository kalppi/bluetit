package dev.jarno.bluetit.clip.cliprequests

interface ClipRequestRepository {
    fun save(clip: ClipRequest)
    fun findById(id: String): ClipRequest?
    fun findAll(): List<ClipRequest>
    fun deleteAll()
}
