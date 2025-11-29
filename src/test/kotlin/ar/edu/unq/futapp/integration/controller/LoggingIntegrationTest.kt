package ar.edu.unq.futapp.integration.controller

import ar.edu.unq.futapp.config.TestDataInitializer
import ar.edu.unq.futapp.events.UpdateTeamNotificationListener
import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.Team
import ar.edu.unq.futapp.repository.TeamRepository
import ar.edu.unq.futapp.repository.UserRepository
import ar.edu.unq.futapp.utils.TestUserUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.layout.PatternLayout
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration-test")
@Import(TestDataInitializer::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LoggingIntegrationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var teamRepository: TeamRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Suppress("unused")
    @MockBean
    lateinit var updateTeamNotificationListener: UpdateTeamNotificationListener

    // Appender de memoria para capturar logs del aspecto
    @Suppress("DEPRECATION")
    private class TestLogAppender(name: String) : AbstractAppender(name, null, PatternLayout.createDefaultLayout(), false) {
        val events = mutableListOf<LogEvent>()
        override fun append(event: LogEvent) {
            events.add(event.toImmutable())
        }
    }

    private fun withAspectLogger(block: (TestLogAppender) -> Unit) {
        val loggerContext = LogManager.getContext(false) as LoggerContext
        val logger = loggerContext.getLogger("ar.edu.unq.futapp.audit.HttpAuditLogAspect")
        val appender = TestLogAppender("TestAppender")
        appender.start()
        logger.addAppender(appender)
        try {
            block(appender)
        } finally {
            logger.removeAppender(appender)
            appender.stop()
        }
    }

    private fun ensureTestUser() {
        val user = TestUserUtils.existingUser()
        if (!userRepository.findByUsername(user.username).isPresent) {
            userRepository.save(ar.edu.unq.futapp.model.User(user.username, passwordEncoder.encode(user.password)))
        }
    }

    @Test
    fun `login ofusca la password en los logs`() {
        ensureTestUser()
        val loginRequest = TestUserUtils.existingUserAuthDto()

        withAspectLogger { appender ->
            mockMvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest))
            )
                .andExpect(status().isOk)

            val logMessage = appender.events.firstOrNull()?.message?.formattedMessage
            Assertions.assertNotNull(logMessage, "No se capturó ningún log de auditoría.")

            Assertions.assertTrue(logMessage!!.contains("audit ts="), "El log de auditoría debe contener 'audit ts='")
            Assertions.assertTrue(logMessage.contains("user=anonymous"), "El log de auditoría debe contener 'user=anonymous'")
            Assertions.assertTrue(logMessage.contains("method=POST"), "El log de auditoría debe contener 'method=POST'")
            Assertions.assertTrue(logMessage.contains("path=/auth/login"), "El log de auditoría debe contener 'path=/auth/login'")
            Assertions.assertTrue(logMessage.contains(loginRequest.username), "El username debe aparecer en los logs")
            Assertions.assertFalse(logMessage.contains(loginRequest.password), "La password en texto plano no debe aparecer en los logs")
            Assertions.assertTrue(logMessage.contains("password=************"), "La password debe aparecer ofuscada")
        }
    }

    @Test
    fun `obtener jugadores de un equipo loguea todos los campos esperados`() {
        ensureTestUser()
        val loginRequest = TestUserUtils.existingUserAuthDto()

        val loginResult = mockMvc.perform(
            MockMvcRequestBuilders.post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andReturn()

        val token = objectMapper.readTree(loginResult.response.contentAsString).get("accessToken").asText()

        val teamName = "Boca"
        val players = listOf(
            Player(name = "Player One", playedGames = 3, goals = 1, assists = 0, rating = 6.7),
            Player(name = "Player Two", playedGames = 6, goals = 0, assists = 1, rating = 6.7)
        )
        val team = Team(name = teamName, players = players.toMutableList())
        teamRepository.save(team)

        withAspectLogger { appender ->
            mockMvc.perform(
                MockMvcRequestBuilders.get("/teams/$teamName/players")
                    .header("Authorization", "Bearer $token")
                    .accept(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)

            val logMessage = appender.events.firstOrNull()?.message?.formattedMessage
            Assertions.assertNotNull(logMessage, "No se capturó ningún log de auditoría para la obtención de jugadores.")

            val encodedTeam = URLEncoder.encode(teamName, StandardCharsets.UTF_8)
            Assertions.assertTrue(logMessage!!.contains("audit ts="), "El log de auditoría debe contener 'audit ts='")
            Assertions.assertTrue(logMessage.contains("user=${loginRequest.username}"), "El log de auditoría debe contener el username del usuario logueado")
            Assertions.assertTrue(logMessage.contains("method=GET"), "El log de auditoría debe contener 'method=GET'")
            Assertions.assertTrue(
                logMessage.contains("path=/teams/$encodedTeam/players") || logMessage.contains("path=/teams/$teamName/players"),
                "El path del equipo debe estar en el log"
            )
        }
    }
}
