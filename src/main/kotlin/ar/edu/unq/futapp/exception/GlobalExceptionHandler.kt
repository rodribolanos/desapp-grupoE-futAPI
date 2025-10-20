package ar.edu.unq.futapp.exception

import ar.edu.unq.futapp.dto.ExceptionDTO
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import jakarta.validation.ConstraintViolationException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionDTO> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Error de validación") }
        val description = errors.toString()
        val exceptionDTO = ExceptionDTO(
            "BAD_REQUEST",
            HttpStatus.BAD_REQUEST.value(),
            description
        )
        return ResponseEntity(exceptionDTO, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ExceptionDTO> {
        val description = ex.constraintViolations
            .joinToString("; ") { v -> "${v.propertyPath}: ${v.message}" }
            .ifBlank { ex.message ?: "Validation failed" }
        val exceptionDTO = ExceptionDTO(
            "BAD_REQUEST",
            HttpStatus.BAD_REQUEST.value(),
            description
        )
        return ResponseEntity(exceptionDTO, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ExceptionDTO> {
        val exceptionDTO = ExceptionDTO(
            "BAD_REQUEST",
            HttpStatus.BAD_REQUEST.value(),
            "Malformed request"
        )
        return ResponseEntity(exceptionDTO, HttpStatus.BAD_REQUEST)
    }

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

    @ExceptionHandler(EntityNotFound::class)
    fun handleEntityNotFound(ex: EntityNotFound): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO("NOT_FOUND", HttpStatus.NOT_FOUND.value(), ex.message ?: "Entidad no encontrada"),
            HttpStatus.NOT_FOUND
        )

    @ExceptionHandler(InvalidRefreshTokenException::class)
    fun handleInvalidRefresh(ex: InvalidRefreshTokenException): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO(
                "UNAUTHORIZED",
                HttpStatus.UNAUTHORIZED.value(),
                ex.message ?: "Refresh token inválido o expirado"
            ),
            HttpStatus.UNAUTHORIZED
        )

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO("BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), ex.message ?: "Solicitud inválida"),
            HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFound(ex: NoResourceFoundException): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO("NOT_FOUND", HttpStatus.NOT_FOUND.value(), ex.message ?: "Recurso no encontrado"),
            HttpStatus.NOT_FOUND
        )

    @ExceptionHandler(ParsingException::class)
    fun handleParsingException(ex: ParsingException): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO(
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.message ?: "Error parsing data"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )

    @ExceptionHandler(InvalidPasswordException::class)
    fun handleInvalidPassword(ex: InvalidPasswordException): ResponseEntity<ExceptionDTO> =
        ResponseEntity(
            ExceptionDTO("BAD_REQUEST", HttpStatus.BAD_REQUEST.value(), ex.message ?: "Contraseña inválida"),
            HttpStatus.BAD_REQUEST
        )

    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<ExceptionDTO> {
        logger.error("Error interno del servidor", ex)
        return ResponseEntity(
            ExceptionDTO(
                "INTERNAL_SERVER_ERROR",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor"
            ),
            HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}