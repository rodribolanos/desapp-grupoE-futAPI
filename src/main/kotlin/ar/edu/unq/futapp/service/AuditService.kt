package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.RequestAudit
import org.springframework.data.domain.Page

interface AuditService {
    fun myHistory(page: Int, size: Int): Page<RequestAudit>
}
