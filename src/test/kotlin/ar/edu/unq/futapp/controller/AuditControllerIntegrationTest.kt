package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.config.TestDataInitializer
import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.Team
import ar.edu.unq.futapp.service.WhoScoredApiClient
import ar.edu.unq.futapp.utils.TestUserUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.Optional
import org.hamcrest.Matchers.greaterThanOrEqualTo

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestDataInitializer::class)
class AuditControllerIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var whoScoredApiClient: WhoScoredApiClient

    // Variables de clase
    private lateinit var accessToken: String
    private val teamName = "Boca Juniors"
    private val encodedTeamName = "Boca%20Juniors"

    @BeforeEach
    fun setup() {
        val loginRequest = TestUserUtils.existingUserAuthDto()
        val loginResult = mockMvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        ).andExpect(status().isOk).andReturn()
        accessToken = objectMapper.readTree(loginResult.response.contentAsString).get("accessToken").asText()

        val fakeTeam = Team(
            name = teamName,
            players = listOf(
                Player("Jugador 1", 10, 3, 2, 6.9),
                Player("Jugador 2", 12, 5, 1, 7.1)
            )
        )
        given(whoScoredApiClient.findTeam(teamName)).willReturn(Optional.of(fakeTeam))
        given(whoScoredApiClient.findUpcomingFixtures(teamName)).willReturn(Optional.of(emptyList()))
    }

    @Test
    @DisplayName("GET /me/history without authentication returns 401")
    fun whenGettingHistoryWithoutAuth_thenUnauthorized() {
        mockMvc.perform(get("/me/history"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    @DisplayName("GET /me/history returns paginated history for authenticated user")
    fun whenGettingHistoryWithAuditedRequests_thenReturnPaginatedHistory() {
        mockMvc.perform(
            get("/teams/$teamName/players").header("Authorization", "Bearer $accessToken")
        ).andExpect(status().isOk)
        mockMvc.perform(
            get("/teams/$teamName/fixtures/upcoming").header("Authorization", "Bearer $accessToken")
        ).andExpect(status().isOk)

        mockMvc.perform(
            get("/me/history?page=0&size=10").header("Authorization", "Bearer $accessToken")
        ).andExpectAll(
            status().isOk,
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.page").value(0),
            jsonPath("$.size").value(10),
            jsonPath("$.totalElements").value(2),
            jsonPath("$.totalPages").value(1),
            jsonPath("$.content.length()").value(2),
            jsonPath("$.content[0].endpoint").value("GET /teams/$encodedTeamName/fixtures/upcoming"),
            jsonPath("$.content[0].status").value(200),
            jsonPath("$.content[0].createdAt").isNotEmpty,
            jsonPath("$.content[0].durationMs").value(greaterThanOrEqualTo(0)),
            jsonPath("$.content[1].endpoint").value("GET /teams/$encodedTeamName/players"),
            jsonPath("$.content[1].status").value(200),
            jsonPath("$.content[1].createdAt").isNotEmpty,
            jsonPath("$.content[1].durationMs").value(greaterThanOrEqualTo(0))
        )

        mockMvc.perform(
            get("/me/history?page=0&size=1").header("Authorization", "Bearer $accessToken")
        ).andExpectAll(
            status().isOk,
            jsonPath("$.page").value(0),
            jsonPath("$.size").value(1),
            jsonPath("$.totalElements").value(2),
            jsonPath("$.totalPages").value(2),
            jsonPath("$.content.length()").value(1),
            jsonPath("$.content[0].endpoint").value("GET /teams/$encodedTeamName/fixtures/upcoming"),
            jsonPath("$.content[0].status").value(200),
            jsonPath("$.content[0].createdAt").isNotEmpty,
            jsonPath("$.content[0].durationMs").value(greaterThanOrEqualTo(0))
        )
    }

    @Test
    @DisplayName("GET /me/history with size <= 0 returns bad request")
    fun whenGettingHistoryWithInvalidSize_thenBadRequest() {
        mockMvc.perform(
            get("/me/history?page=0&size=0").header("Authorization", "Bearer $accessToken")
        ).andExpectAll(
            status().isBadRequest,
            content().contentType(MediaType.APPLICATION_JSON)
        )
        mockMvc.perform(
            get("/me/history?page=0&size=-1").header("Authorization", "Bearer $accessToken")
        ).andExpectAll(
            status().isBadRequest,
            content().contentType(MediaType.APPLICATION_JSON)
        )
    }
}
