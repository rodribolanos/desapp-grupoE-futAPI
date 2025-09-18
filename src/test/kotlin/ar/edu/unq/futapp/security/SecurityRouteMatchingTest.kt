package ar.edu.unq.futapp.security

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
@ActiveProfiles("test")
class SecurityRouteMatchingTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `auth routes are permitted even if handler is missing`() {
        // Al no existir un handler, si pasa seguridad debe ser 404, no 401/403
        mockMvc.perform(get("/auth/does-not-exist"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `non auth routes require authentication`() {
        // Sin autenticación, rutas fuera de /auth deben dar 401
        mockMvc.perform(get("/does-not-exist"))
            .andExpect(status().isUnauthorized)
    }
}

