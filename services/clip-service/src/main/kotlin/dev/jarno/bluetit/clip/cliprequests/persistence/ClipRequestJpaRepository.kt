package dev.jarno.bluetit.clip.cliprequests.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ClipRequestJpaRepository : JpaRepository<ClipRequestEntity, String>