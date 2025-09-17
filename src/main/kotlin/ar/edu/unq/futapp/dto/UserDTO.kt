package ar.edu.unq.futapp.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UserDTO(
    @field:NotBlank(message = "El nombre de usuario no puede estar vacío")
    @field:Size(min = 4, max = 50, message = "El nombre de usuario debe tener entre 4 y 50 caracteres")
    val username: String
)