package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.beans.JwtUtil
import ar.edu.unq.futapp.model.AuthRequest
import ar.edu.unq.futapp.model.AuthResponse
import ar.edu.unq.futapp.model.RefreshRequest
import ar.edu.unq.futapp.exception.InvalidCredentialsException
import ar.edu.unq.futapp.exception.InvalidRefreshTokenException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    private val userService: UserService,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) : AuthService {
    override fun login(request: AuthRequest): AuthResponse {
        try {
            val authToken = UsernamePasswordAuthenticationToken(request.username, request.password)
            authenticationManager.authenticate(authToken)
            val accessToken = jwtUtil.generateToken(request.username)
            val refreshToken = jwtUtil.generateToken(request.username, isRefresh = true)
            return AuthResponse(accessToken, refreshToken)
        } catch (e: AuthenticationException) {
            throw InvalidCredentialsException("Credenciales inválidas")
        }
    }

    override fun register(request: AuthRequest): AuthResponse {
        userService.register(request.username, request.password)
        val accessToken = jwtUtil.generateToken(request.username)
        val refreshToken = jwtUtil.generateToken(request.username, isRefresh = true)
        return AuthResponse(accessToken, refreshToken)
    }

    override fun refresh(request: RefreshRequest): AuthResponse {
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
        return AuthResponse(newAccessToken, newRefreshToken)
    }
}
