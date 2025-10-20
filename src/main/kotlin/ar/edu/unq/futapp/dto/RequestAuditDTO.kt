package ar.edu.unq.futapp.dto

import ar.edu.unq.futapp.model.RequestAudit
import java.time.Instant

data class RequestAuditDTO(
    val endpoint: String,
    val status: Int,
    val durationMs: Long,
    val createdAt: Instant
)

fun RequestAudit.toDTO(): RequestAuditDTO = RequestAuditDTO(
    endpoint = this.getEndpoint(),
    status = this.getStatus(),
    durationMs = this.getDurationMs(),
    createdAt = this.getCreatedAt()
)

