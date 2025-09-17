package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.dto.AuthRequestDTO
import ar.edu.unq.futapp.dto.RefreshRequestDTO
import ar.edu.unq.futapp.model.User
import ar.edu.unq.futapp.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val username = "integrationUser"
    private val password = "Test123!"
    private val invalidPassword = "short"

    @BeforeEach
    fun setup() {
        userRepository.deleteAll()
    }

    @Test
    fun whenRegisterWithValidUser_thenReturnSuccess() {
        // Arrange
        val request = AuthRequestDTO(username, password)
        // Act & Assert
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
    }

    @Test
    fun whenRegisterWithExistingUser_thenReturnConflict() {
        // Arrange
        userRepository.save(User(username, password))
        val request = AuthRequestDTO(username, password)
        // Act & Assert
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("CONFLICT"))
            .andExpect(jsonPath("$.status").value(409))
    }

    @Test
    fun whenRegisterWithInvalidPassword_thenReturnBadRequest() {
        // Arrange
        val request = AuthRequestDTO(username, invalidPassword)
        // Act & Assert
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("BAD_REQUEST"))
            .andExpect(jsonPath("$.status").value(400))
    }

    @Test
    fun whenLoginWithValidCredentials_thenReturnTokens() {
        // Arrange
        userRepository.save(User(username, password))
        val request = AuthRequestDTO(username, password)
        // Act & Assert
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
    }

    @Test
    fun whenLoginWithInvalidCredentials_thenReturnUnauthorized() {
        // Arrange
        userRepository.save(User(username, password))
        val request = AuthRequestDTO(username, "WrongPass123!")
        // Act & Assert
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value("UNAUTHORIZED"))
            .andExpect(jsonPath("$.status").value(401))
    }

    @Test
    fun whenLoginWithNonExistingUser_thenReturnUnauthorized() {
        // Arrange
        val request = AuthRequestDTO("nouser", password)
        // Act & Assert
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value("UNAUTHORIZED"))
            .andExpect(jsonPath("$.status").value(401))
    }

    @Test
    fun whenRefreshWithValidToken_thenReturnNewTokens() {
        // Arrange
        userRepository.save(User(username, password))
        val loginRequest = AuthRequestDTO(username, password)
        val loginResult = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andReturn()
        val refreshToken = objectMapper.readTree(loginResult.response.contentAsString).get("refreshToken").asText()
        val refreshRequest = RefreshRequestDTO(refreshToken)
        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accessToken").exists())
            .andExpect(jsonPath("$.refreshToken").exists())
    }

    @Test
    fun whenRefreshWithInvalidToken_thenReturnUnauthorized() {
        // Arrange
        val refreshRequest = RefreshRequestDTO("invalidtoken")
        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value("UNAUTHORIZED"))
            .andExpect(jsonPath("$.status").value(401))
    }

    @Test
    fun whenRefreshWithAccessToken_thenReturnUnauthorized() {
        // Arrange
        userRepository.save(User(username, password))
        val loginRequest = AuthRequestDTO(username, password)
        val loginResult = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andReturn()
        val accessToken = objectMapper.readTree(loginResult.response.contentAsString).get("accessToken").asText()
        val refreshRequest = RefreshRequestDTO(accessToken)
        // Act & Assert
        mockMvc.perform(post("/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value("UNAUTHORIZED"))
            .andExpect(jsonPath("$.status").value(401))
    }
}
