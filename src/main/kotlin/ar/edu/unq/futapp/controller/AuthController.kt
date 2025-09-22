package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.dto.AuthRequestDTO
import ar.edu.unq.futapp.dto.AuthResponseDTO
import ar.edu.unq.futapp.dto.RefreshRequestDTO
import ar.edu.unq.futapp.dto.toDTO
import ar.edu.unq.futapp.service.AuthService
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
    @PostMapping("/login")
    fun login(@RequestBody @Valid request: AuthRequestDTO): ResponseEntity<AuthResponseDTO> =
        ResponseEntity.ok(authService.login(request.toModel()).toDTO())

    @PostMapping("/register")
    fun register(@RequestBody @Valid request: AuthRequestDTO): ResponseEntity<AuthResponseDTO> =
        ResponseEntity(authService.register(request.toModel()).toDTO(), HttpStatus.CREATED)

    @PostMapping("/refresh")
    fun refresh(@RequestBody @Valid request: RefreshRequestDTO): ResponseEntity<AuthResponseDTO> =
        ResponseEntity.ok(authService.refresh(request.toModel()).toDTO())
}
