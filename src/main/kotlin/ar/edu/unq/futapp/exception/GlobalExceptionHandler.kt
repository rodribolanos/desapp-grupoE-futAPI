package ar.edu.unq.futapp.exception

import ar.edu.unq.futapp.dto.ExceptionDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(ex: InvalidCredentialsException): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO("BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), ex.message ?: "Credenciales inválidas"),
            HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserExists(ex: UserAlreadyExistsException): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO("CONFLICT", HttpStatus.CONFLICT.value(), ex.message ?: "El usuario ya existe"),
            HttpStatus.CONFLICT
        )

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleInvalidRefresh(ex: InvalidRefreshTokenException): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO("UNAUTHORIZED", HttpStatus.UNAUTHORIZED.value(), ex.message ?: "Refresh token inválido o expirado"),
            HttpStatus.UNAUTHORIZED
        )

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO("BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), ex.message ?: "Solicitud inválida"),
            HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(InvalidPasswordException::class)
    fun handleInvalidPassword(ex: InvalidPasswordException): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO("BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), ex.message ?: "Contraseña inválida"),
            HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(ex: NoResourceFoundException): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO("NOT_FOUND", HttpStatus.NOT_FOUND.value(), ex.message ?: "Recurso no encontrado"),
            HttpStatus.NOT_FOUND
        )

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message ?: "Error interno del servidor"),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
}