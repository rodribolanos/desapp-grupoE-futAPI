package ar.edu.unq.futapp.repository

import ar.edu.unq.futapp.model.RequestAudit
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RequestAuditRepository : JpaRepository<RequestAudit, Long> {
    fun findByUserId(userId: String, pageable: Pageable): Page<RequestAudit>
}
