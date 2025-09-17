package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.config.JwtUtil
import ar.edu.unq.futapp.dto.AuthRequestDTO
import ar.edu.unq.futapp.dto.RefreshRequestDTO
import ar.edu.unq.futapp.exception.InvalidCredentialsException
import ar.edu.unq.futapp.exception.InvalidRefreshTokenException
import ar.edu.unq.futapp.exception.UserAlreadyExistsException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class AuthServiceTest {
    private lateinit var userService: UserService
    private lateinit var jwtUtil: JwtUtil
    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var authService: AuthService

    private val username = "testUser"
    private val password = "Test123!"
    private val accessToken = "access-token"
    private val refreshToken = "refresh-token"

    @BeforeEach
    fun setup() {
        userService = mock(UserService::class.java)
        jwtUtil = mock(JwtUtil::class.java)
        authenticationManager = mock(AuthenticationManager::class.java)
        authService = AuthService(userService, jwtUtil, authenticationManager)
    }

    @Test
    fun whenLoginWithValidCredentials_thenReturnTokens() {
        // Arrange
        doNothing().`when`(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken::class.java))
        `when`(jwtUtil.generateToken(username)).thenReturn(accessToken)
        `when`(jwtUtil.generateToken(username, true)).thenReturn(refreshToken)
        val request = AuthRequestDTO(username, password)
        // Act
        val response = authService.login(request)
        // Assert
        assert(response.accessToken == accessToken)
        assert(response.refreshToken == refreshToken)
    }

    @Test
    fun whenLoginWithInvalidCredentials_thenThrowInvalidCredentialsException() {
        // Arrange
        doThrow(org.springframework.security.authentication.BadCredentialsException("Credenciales inválidas")).`when`(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken::class.java))
        val request = AuthRequestDTO(username, password)
        // Act & Assert
        assertThrows<InvalidCredentialsException> { authService.login(request) }
    }

    @Test
    fun whenRegisterWithNewUser_thenReturnSuccessMessage() {
        // Arrange
        `when`(userService.register(username, password)).thenReturn(true)
        val request = AuthRequestDTO(username, password)
        // Act
        val response = authService.register(request)
        // Assert
        assert(response.message == "Usuario registrado correctamente")
    }

    @Test
    fun whenRegisterWithExistingUser_thenThrowUserAlreadyExistsException() {
        // Arrange
        `when`(userService.register(username, password)).thenReturn(false)
        val request = AuthRequestDTO(username, password)
        // Act & Assert
        assertThrows<UserAlreadyExistsException> { authService.register(request) }
    }

    @Test
    fun whenRefreshWithValidToken_thenReturnNewTokens() {
        // Arrange
        val request = AuthRequestDTO(username, password) // Usar el DTO adecuado para refresh
        `when`(jwtUtil.validateToken(refreshToken)).thenReturn(true)
        `when`(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true)
        val request = RefreshRequestDTO(refreshToken)
        `when`(jwtUtil.generateToken(username)).thenReturn(accessToken)
        `when`(jwtUtil.generateToken(username, true)).thenReturn(refreshToken)
        // Act
        val response = authService.refresh(refreshToken)
        // Assert
        assert(response.accessToken == accessToken)
        val response = authService.refresh(request)
    }

    @Test
    fun whenRefreshWithInvalidToken_thenThrowInvalidRefreshTokenException() {
        // Arrange
        `when`(jwtUtil.validateToken(refreshToken)).thenReturn(false)
        // Act & Assert
        assertThrows<InvalidRefreshTokenException> { authService.refresh(refreshToken) }
        val request = RefreshRequestDTO(refreshToken)
    }

        assertThrows<InvalidRefreshTokenException> { authService.refresh(request) }
    fun whenRefreshWithNonRefreshToken_thenThrowInvalidRefreshTokenException() {
        // Arrange
        `when`(jwtUtil.validateToken(accessToken)).thenReturn(true)
        `when`(jwtUtil.isRefreshToken(accessToken)).thenReturn(false)
        // Act & Assert
        val request = RefreshRequestDTO(accessToken)
        assertThrows<InvalidRefreshTokenException> { authService.refresh(accessToken) }
    }

        assertThrows<InvalidRefreshTokenException> { authService.refresh(request) }
    fun whenRefreshWithNullUsername_thenThrowInvalidRefreshTokenException() {
        // Arrange
        `when`(jwtUtil.validateToken(refreshToken)).thenReturn(true)
        `when`(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true)
        `when`(jwtUtil.getUsername(refreshToken)).thenReturn(java.util.Optional.empty())
        val request = RefreshRequestDTO(refreshToken)
        // Act & Assert
        assertThrows<InvalidRefreshTokenException> { authService.refresh(refreshToken) }
    }
}
        assertThrows<InvalidRefreshTokenException> { authService.refresh(request) }
