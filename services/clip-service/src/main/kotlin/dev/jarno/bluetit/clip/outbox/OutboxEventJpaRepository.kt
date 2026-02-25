package dev.jarno.bluetit.clip.outbox

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface OutboxEventJpaRepository : JpaRepository<OutboxEventEntity, UUID> {

    @Query(
        value = """
            select e from OutboxEventEntity e
            where e.publishedAt is null
            order by e.occurredAt asc
        """,
    )
    fun findUnpublished(): List<OutboxEventEntity>

    @Query(
        value = """
            select e from OutboxEventEntity e
            where e.publishedAt is null
            order by e.occurredAt asc
        """,
    )
    fun findUnpublishedLimited(@Param("limit") limit: Int): List<OutboxEventEntity>
}