package dev.jarno.bluetit.clip.cliprequests.persistence

import dev.jarno.bluetit.clip.cliprequests.ClipRequest
import dev.jarno.bluetit.clip.cliprequests.ClipRequestRepository
import org.springframework.stereotype.Repository

@Repository
class JpaClipRequestRepository(
    private val jpa: ClipRequestJpaRepository,
) : ClipRequestRepository {

    override fun save(clip: ClipRequest) {
        jpa.save(ClipRequestEntity.fromDomain(clip))
    }

    override fun findById(id: String): ClipRequest? {
        val entity = jpa.findById(id).orElse(null)
        if (entity == null) {
            return null
        }
        return entity.toDomain()
    }

    override fun findAll(): List<ClipRequest> {
        return jpa.findAll().map { it.toDomain() }
    }

    override fun deleteAll() {
        jpa.deleteAll()
    }
}