package ar.edu.unq.futapp.integration.security

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
class SecurityRouteMatchingTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    @DisplayName("Auth routes are public and return 404 if no handler exists")
    fun whenGettingAuthPrefixRoute_thenDoesntAskForAuth() {
        // Al no existir un handler, si pasa seguridad debe ser 404, no 401/403
        mockMvc.perform(get("/auth/does-not-exist"))
            .andExpect(status().isNotFound)
    }

    @Test
    @DisplayName("Non-auth routes require authentication and return 401 if not logged in")
    fun whenGettingDefaultRoute_thenAskForAuth() {
        // Sin autenticación, rutas fuera de /auth deben dar 401
        mockMvc.perform(get("/does-not-exist"))
            .andExpect(status().isUnauthorized)
    }
}
