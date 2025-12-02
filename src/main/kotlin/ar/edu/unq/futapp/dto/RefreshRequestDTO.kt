package ar.edu.unq.futapp.dto

import ar.edu.unq.futapp.audit.Sensitive
import ar.edu.unq.futapp.model.RefreshRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RefreshRequestDTO(
    @field:NotBlank(message = "El refreshToken no puede estar vacío")
    @field:Size(min = 10, max = 500, message = "El refreshToken debe tener entre 10 y 500 caracteres")
    @field:Sensitive
    val refreshToken: String
) {
    fun toModel() = RefreshRequest(refreshToken)
}
