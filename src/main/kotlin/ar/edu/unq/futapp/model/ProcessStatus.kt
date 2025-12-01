package ar.edu.unq.futapp.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "async_process_status")
data class ProcessStatus(
    @Id
    val id: String,
    @Enumerated(EnumType.STRING)
    var status: Status = Status.IN_PROCESS,
    @Column(columnDefinition = "TEXT")
    var resultJson: String? = null,
    var errorMessage: String? = null,
    val createdAt: Instant = Instant.now()
)
