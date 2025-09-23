package ar.edu.unq.futapp.dto

import ar.edu.unq.futapp.model.AuthResponse
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AuthResponseDTO(
    @field:NotBlank(message = "El accessToken no puede estar vacío")
    @field:Size(min = 10, max = 500, message = "El accessToken debe tener entre 10 y 500 caracteres")
    val accessToken: String,
    @field:NotBlank(message = "El refreshToken no puede estar vacío")
    @field:Size(min = 10, max = 500, message = "El refreshToken debe tener entre 10 y 500 caracteres")
    val refreshToken: String
) {
    companion object {
        fun fromModel(model: AuthResponse) = AuthResponseDTO(model.accessToken, model.refreshToken)
    }
}

fun AuthResponse.toDTO() = AuthResponseDTO(accessToken, refreshToken)
