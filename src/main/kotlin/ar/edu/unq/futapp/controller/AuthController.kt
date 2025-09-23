package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.dto.*
import ar.edu.unq.futapp.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/auth")
class AuthController @Autowired constructor(
    private val authService: AuthService
) {
    @Operation(summary = "User login", description = "Authenticate user and return JWT tokens.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully authenticated",
                content = [Content(schema = Schema(implementation = AuthResponseDTO::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid credentials",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            )
        ]
    )
    @PostMapping("/login")
    fun login(@RequestBody @Valid request: AuthRequestDTO): ResponseEntity<AuthResponseDTO> =
        ResponseEntity.ok(authService.login(request.toModel()).toDTO())


    @Operation(summary = "User registration", description = "Register a new user and return JWT tokens.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Successfully registered",
                content = [Content(schema = Schema(implementation = AuthResponseDTO::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid password format or missing fields",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            ),
            ApiResponse(
                responseCode = "409",
                description = "User already exists",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            ),
        ]
    )
    @PostMapping("/register")
    fun register(@RequestBody @Valid request: AuthRequestDTO): ResponseEntity<AuthResponseDTO> =
        ResponseEntity(authService.register(request.toModel()).toDTO(), HttpStatus.CREATED)

    @Operation(summary = "Refresh JWT tokens", description = "Refresh access and refresh tokens using a valid refresh token.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully refreshed tokens",
                content = [Content(schema = Schema(implementation = AuthResponseDTO::class))]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Invalid refresh token",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]),
        ]
    )
    @PostMapping("/refresh")
    fun refresh(@RequestBody @Valid request: RefreshRequestDTO): ResponseEntity<AuthResponseDTO> =
        ResponseEntity.ok(authService.refresh(request.toModel()).toDTO())
}
