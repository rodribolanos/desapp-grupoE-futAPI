package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.beans.JwtUtil
import ar.edu.unq.futapp.exception.InvalidCredentialsException
import ar.edu.unq.futapp.exception.InvalidRefreshTokenException
import ar.edu.unq.futapp.exception.UserAlreadyExistsException
import ar.edu.unq.futapp.model.AuthRequest
import ar.edu.unq.futapp.model.AuthResponse
import ar.edu.unq.futapp.model.RefreshRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import java.util.*
import kotlin.test.assertEquals

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
        authService = AuthServiceImpl(userService, jwtUtil, authenticationManager)
    }

    @Test
    @DisplayName("Login with valid credentials returns tokens")
    fun whenLoginWithValidCredentials_thenReturnTokens() {
        // Arrange
        val expected = AuthResponse(accessToken, refreshToken)
        `when`(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
            .thenReturn(UsernamePasswordAuthenticationToken(username, password))
        `when`(jwtUtil.generateToken(username)).thenReturn(accessToken)
        `when`(jwtUtil.generateToken(username, true)).thenReturn(refreshToken)
        val request = AuthRequest(username, password)
        // Act
        val response = authService.login(request)
        // Assert (comparación de objeto completo)
        assertEquals(expected, response)
    }

    @Test
    @DisplayName("Login with invalid credentials throws InvalidCredentialsException")
    fun whenLoginWithInvalidCredentials_thenThrowInvalidCredentialsException() {
        // Arrange
        `when`(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken::class.java)))
            .thenThrow(org.springframework.security.authentication.BadCredentialsException("bad"))
        val request = AuthRequest(username, password)
        // Act & Assert
        assertThrows<InvalidCredentialsException> { authService.login(request) }
    }

    @Test
    @DisplayName("Registering a new user returns tokens")
    fun whenRegisterWithNewUser_thenReturnTokens() {
        // Arrange
        val expected = AuthResponse(accessToken, refreshToken)
        doNothing().`when`(userService).register(username, password)
        `when`(jwtUtil.generateToken(username)).thenReturn(accessToken)
        `when`(jwtUtil.generateToken(username, true)).thenReturn(refreshToken)
        val request = AuthRequest(username, password)
        // Act
        val response = authService.register(request)
        // Assert
        assertEquals(expected, response)
    }

    @Test
    @DisplayName("Registering an existing user throws UserAlreadyExistsException")
    fun whenRegisterWithExistingUser_thenThrowUserAlreadyExistsException() {
        // Arrange
        doThrow(UserAlreadyExistsException("El usuario ya existe")).`when`(userService).register(username, password)
        val request = AuthRequest(username, password)
        // Act & Assert
        assertThrows<UserAlreadyExistsException> { authService.register(request) }
    }

    @Test
    @DisplayName("Refreshing with a valid token returns new tokens")
    fun whenRefreshWithValidToken_thenReturnNewTokens() {
        // Arrange
        val expected = AuthResponse(accessToken, "new-$refreshToken")
        val refreshReq = RefreshRequest(refreshToken)
        `when`(jwtUtil.validateToken(refreshToken)).thenReturn(true)
        `when`(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true)
        `when`(jwtUtil.getUsername(refreshToken)).thenReturn(Optional.of(username))
        `when`(jwtUtil.generateToken(username)).thenReturn(accessToken)
        `when`(jwtUtil.generateToken(username, true)).thenReturn("new-$refreshToken")
        // Act
        val response = authService.refresh(refreshReq)
        // Assert
        assertEquals(expected, response)
    }

    @Test
    @DisplayName("Refreshing with an invalid token throws InvalidRefreshTokenException")
    fun whenRefreshWithInvalidToken_thenThrowInvalidRefreshTokenException() {
        // Arrange
        val refreshReq = RefreshRequest(refreshToken)
        `when`(jwtUtil.validateToken(refreshToken)).thenReturn(false)
        // Act & Assert
        assertThrows<InvalidRefreshTokenException> { authService.refresh(refreshReq) }
    }

    @Test
    @DisplayName("Refreshing with a non-refresh token throws InvalidRefreshTokenException")
    fun whenRefreshWithNonRefreshToken_thenThrowInvalidRefreshTokenException() {
        // Arrange
        val refreshReq = RefreshRequest(accessToken)
        `when`(jwtUtil.validateToken(accessToken)).thenReturn(true)
        `when`(jwtUtil.isRefreshToken(accessToken)).thenReturn(false)
        // Act & Assert
        assertThrows<InvalidRefreshTokenException> { authService.refresh(refreshReq) }
    }

    @Test
    @DisplayName("Refreshing with a valid token but null username throws InvalidRefreshTokenException")
    fun whenRefreshWithNullUsername_thenThrowInvalidRefreshTokenException() {
        // Arrange
        val refreshReq = RefreshRequest(refreshToken)
        `when`(jwtUtil.validateToken(refreshToken)).thenReturn(true)
        `when`(jwtUtil.isRefreshToken(refreshToken)).thenReturn(true)
        `when`(jwtUtil.getUsername(refreshToken)).thenReturn(Optional.empty())
        // Act & Assert
        assertThrows<InvalidRefreshTokenException> { authService.refresh(refreshReq) }
    }
}
