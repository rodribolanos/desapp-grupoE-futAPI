package ar.edu.unq.futapp.dto

import ar.edu.unq.futapp.model.ProcessStatus
import ar.edu.unq.futapp.model.Status
import ar.edu.unq.futapp.model.TeamComparisonResult
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Instant

data class ProcessStatusDTO(
    val id: String,
    val status: Status,
    val comparison: TeamComparisonResult? = null,
    val errorMessage: String? = null,
    val createdAt: Instant
)


fun ProcessStatus.toDTO(objectMapper: ObjectMapper): ProcessStatusDTO {
    val comparisonResult: TeamComparisonResult? = if (this.status == Status.FINISHED && this.resultJson != null) {
        try {
            objectMapper.readValue(this.resultJson!!, TeamComparisonResult::class.java)
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }

    return ProcessStatusDTO(
        id = this.id,
        status = this.status,
        comparison = comparisonResult,
        errorMessage = this.errorMessage,
        createdAt = this.createdAt
    )
}