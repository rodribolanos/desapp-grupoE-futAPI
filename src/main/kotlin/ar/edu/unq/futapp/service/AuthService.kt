package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.config.JwtUtil
import ar.edu.unq.futapp.dto.AuthRequestDTO
import ar.edu.unq.futapp.dto.AuthResponseDTO
import ar.edu.unq.futapp.dto.RefreshRequestDTO
import ar.edu.unq.futapp.exception.InvalidCredentialsException
import ar.edu.unq.futapp.exception.InvalidRefreshTokenException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userService: UserService,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {
    fun login(request: AuthRequestDTO): AuthResponseDTO {
        try {
            val authToken = UsernamePasswordAuthenticationToken(request.username, request.password)
            authenticationManager.authenticate(authToken)
            val accessToken = jwtUtil.generateToken(request.username)
            val refreshToken = jwtUtil.generateToken(request.username, isRefresh = true)
            return AuthResponseDTO(accessToken, refreshToken)
        } catch (e: AuthenticationException) {
            throw InvalidCredentialsException("Credenciales inválidas")
        }
    }

    fun register(request: AuthRequestDTO): AuthResponseDTO {
        userService.register(request.username, request.password)
        val accessToken = jwtUtil.generateToken(request.username)
        val refreshToken = jwtUtil.generateToken(request.username, isRefresh = true)
        return AuthResponseDTO(accessToken, refreshToken)
    }

    fun refresh(request: RefreshRequestDTO): AuthResponseDTO {
        val token = request.refreshToken
        if (!jwtUtil.validateToken(token) || !jwtUtil.isRefreshToken(token)) {
            throw InvalidRefreshTokenException("Refresh token inválido o expirado")
        }
        val usernameOpt = jwtUtil.getUsername(token)
        if (usernameOpt.isEmpty) {
            throw InvalidRefreshTokenException("Refresh token inválido")
        }
        val username = usernameOpt.get()
        val newAccessToken = jwtUtil.generateToken(username)
        val newRefreshToken = jwtUtil.generateToken(username, isRefresh = true)
        return AuthResponseDTO(newAccessToken, newRefreshToken)
    }
}
