package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.dto.AuthRequestDTO
import ar.edu.unq.futapp.dto.AuthResponseDTO
import ar.edu.unq.futapp.dto.RefreshRequestDTO
import ar.edu.unq.futapp.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController @Autowired constructor(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequestDTO): ResponseEntity<AuthResponseDTO> =
        ResponseEntity.ok(authService.login(request))

    @PostMapping("/register")
    fun register(@RequestBody request: AuthRequestDTO): ResponseEntity<AuthResponseDTO> =
        ResponseEntity(authService.register(request), HttpStatus.CREATED)

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshRequestDTO): ResponseEntity<AuthResponseDTO> =
        ResponseEntity.ok(authService.refresh(request))
}
