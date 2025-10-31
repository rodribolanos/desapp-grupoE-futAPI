package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.config.TestDataInitializer
import ar.edu.unq.futapp.dto.RefreshRequestDTO
import ar.edu.unq.futapp.utils.TestUserUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Import(TestDataInitializer::class)
class AuthControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    @DisplayName("Registering a valid user returns success and tokens")
    fun whenRegisterWithValidUser_thenReturnSuccess() {
        val request = TestUserUtils.unregisteredUserAuthDto()
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpectAll(
                status().isCreated,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.accessToken").isNotEmpty,
                jsonPath("$.refreshToken").isNotEmpty
            )
    }

    @Test
    @DisplayName("Registering an existing user returns conflict")
    fun whenRegisterWithExistingUser_thenReturnConflict() {
        val request = TestUserUtils.existingUserAuthDto()
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpectAll(
                status().isConflict,
                content().contentType(MediaType.APPLICATION_JSON)
            )
    }

    @Test
    @DisplayName("Registering with invalid password returns bad request")
    fun whenRegisterWithInvalidPassword_thenReturnBadRequest() {
        val request = TestUserUtils.invalidUserAuthDto()
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpectAll(
                status().isBadRequest,
                content().contentType(MediaType.APPLICATION_JSON)
            )
    }

    @Test
    @DisplayName("Login with valid credentials returns tokens")
    fun whenLoginWithValidCredentials_thenReturnTokens() {
        val request = TestUserUtils.existingUserAuthDto()
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpectAll(
                status().isOk,
                jsonPath("$.accessToken").isNotEmpty,
                jsonPath("$.refreshToken").isNotEmpty,
                content().contentType(MediaType.APPLICATION_JSON)
            )
    }

    @Test
    @DisplayName("Login with invalid credentials returns error")
    fun whenLoginWithInvalidCredentials_thenReturnInvalidCredentials() {
        val wrongAuth = TestUserUtils.invalidExistingUserAuthDto()
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(wrongAuth)))
            .andExpectAll(
                status().isBadRequest,
                content().contentType(MediaType.APPLICATION_JSON)
            )
    }

    @Test
    @DisplayName("Login with non-existing user returns error")
    fun whenLoginWithNonExistingUser_thenReturnInvalidCredentials() {
        val request = TestUserUtils.unexistingUserAuthDto()
        mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpectAll(
                status().isBadRequest,
                content().contentType(MediaType.APPLICATION_JSON)
            )
    }

    @Test
    @DisplayName("Refreshing with valid token returns new tokens")
    fun whenRefreshWithValidToken_thenReturnNewTokens() {
        val loginRequest = TestUserUtils.existingUserAuthDto()
        val loginResult = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andReturn()
        val oldRefreshToken = objectMapper.readTree(loginResult.response.contentAsString).get("refreshToken").asText()
        val refreshRequest = RefreshRequestDTO(oldRefreshToken)
        mockMvc.perform(post("/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.accessToken").isNotEmpty,
                jsonPath("$.refreshToken").isNotEmpty
            )
    }

    @Test
    @DisplayName("Refreshing with invalid token returns unauthorized")
    fun whenRefreshWithInvalidToken_thenReturnUnauthorized() {
        val refreshRequest = RefreshRequestDTO("invalidtoken")
        mockMvc.perform(post("/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpectAll(
                status().isUnauthorized,
                content().contentType(MediaType.APPLICATION_JSON)
            )
    }

    @Test
    @DisplayName("Refreshing with access token returns unauthorized")
    fun whenRefreshWithAccessToken_thenReturnUnauthorized() {
        val loginRequest = TestUserUtils.existingUserAuthDto()
        val loginResult = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginRequest)))
            .andReturn()
        val accessToken = objectMapper.readTree(loginResult.response.contentAsString).get("accessToken").asText()
        val refreshRequest = RefreshRequestDTO(accessToken)
        mockMvc.perform(post("/auth/refresh")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(refreshRequest)))
            .andExpectAll(
                status().isUnauthorized,
                content().contentType(MediaType.APPLICATION_JSON)
            )
    }
}
