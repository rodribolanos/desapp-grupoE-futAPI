package ar.edu.unq.futapp.service.impl

import ar.edu.unq.futapp.beans.AuthUtils
import ar.edu.unq.futapp.model.RequestAudit
import ar.edu.unq.futapp.repository.RequestAuditRepository
import ar.edu.unq.futapp.service.AuditService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class AuditServiceImpl(
    private val repository: RequestAuditRepository,
    private val authUtils: AuthUtils
) : AuditService {

    override fun myHistory(page: Int, size: Int): Page<RequestAudit> {
        val userId = authUtils.getCurrentUser().username()
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        return repository.findByUserId(userId, pageable)
    }
}
