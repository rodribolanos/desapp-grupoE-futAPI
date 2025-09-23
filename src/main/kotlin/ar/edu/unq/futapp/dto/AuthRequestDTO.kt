package ar.edu.unq.futapp.dto

import ar.edu.unq.futapp.model.AuthRequest
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AuthRequestDTO(
    @field:NotBlank(message = "El nombre de usuario no puede estar vacío")
    @field:Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    val username: String,
    @field:NotBlank(message = "La contraseña no puede estar vacía")
    @field:Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
    val password: String
) {
    fun toModel() = AuthRequest(username, password)
}
