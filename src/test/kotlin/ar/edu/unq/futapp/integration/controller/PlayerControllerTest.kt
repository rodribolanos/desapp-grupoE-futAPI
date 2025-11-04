package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.builders.PerformanceBuilder
import ar.edu.unq.futapp.builders.PlayerPerformanceBuilder
import ar.edu.unq.futapp.config.TestDataInitializer
import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.exception.InternalServerException
import ar.edu.unq.futapp.service.PlayerService
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestDataInitializer::class)
class PlayerControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var playerService: PlayerService

    @Test
    @DisplayName("Get player performance returns success when player exists")
    @WithMockUser(
        username = "testuser",
        roles = ["USER"]
    )
    fun whenGetPlayerPerformanceWithValidName_thenReturnSuccess() {
        val playerName = "Lionel Messi"
        val seasonPerformance = PerformanceBuilder()
            .withSeason("2023-24")
            .withTeam("Barcelona")
            .withCompetition("La Liga")
            .withAppearances(25)
            .withGoals(15)
            .withAssists(10)
            .withAerialWons(2.5)
            .withRating(8.5)
            .build()

        val playerPerformance = PlayerPerformanceBuilder()
            .withName(playerName)
            .withSeasons(listOf(seasonPerformance))
            .build()

        every { playerService.findPlayerPerformanceByName(playerName) } returns playerPerformance

        mockMvc.perform(get("/players/{playerName}/performance", playerName)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.name").value(playerName),
                jsonPath("$.seasons").isArray,
                jsonPath("$.seasons[0].season").value("2023-24"),
                jsonPath("$.seasons[0].team").value("Barcelona"),
                jsonPath("$.seasons[0].goals").value(15)
            )
    }

    @Test
    @DisplayName("Get player performance returns 404 when player not found")
    @WithMockUser
    fun whenGetPlayerPerformanceWithNonExistingPlayer_thenReturnNotFound() {
        val playerName = "Unknown Player"

        every { playerService.findPlayerPerformanceByName(playerName) } throws
                EntityNotFound("Player not found: $playerName")

        mockMvc.perform(get("/players/{playerName}/performance", playerName)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isNotFound,
                content().contentType(MediaType.APPLICATION_JSON)
            )
    }

    @Test
    @DisplayName("Get player performance returns 400 when player name is blank")
    @WithMockUser
    fun whenGetPlayerPerformanceWithBlankName_thenReturnBadRequest() {

        mockMvc.perform(get("/players/{playerName}/performance", " ")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isBadRequest,
                content().contentType(MediaType.APPLICATION_JSON)
            )
    }

    @Test
    @DisplayName("Get player performance returns 500 when internal error occurs")
    @WithMockUser
    fun whenGetPlayerPerformanceWithInternalError_thenReturnInternalServerError() {
        val playerName = "Cristiano Ronaldo"

        every { playerService.findPlayerPerformanceByName(playerName) } throws
                InternalServerException("Error connecting to WhoScored")

        mockMvc.perform(get("/players/{playerName}/performance", playerName)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isInternalServerError,
                content().contentType(MediaType.APPLICATION_JSON)
            )
    }

    @Test
    @DisplayName("Get player performance returns 401 when no token provided")
    fun whenGetPlayerPerformanceWithoutToken_thenReturnUnauthorized() {
        val playerName = "Kylian Mbappe"

        mockMvc.perform(get("/players/{playerName}/performance", playerName)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isUnauthorized,
            )
    }

    @Test
    @DisplayName("Get player performance returns success with multiple seasons")
    @WithMockUser
    fun whenGetPlayerPerformanceWithMultipleSeasons_thenReturnSuccess() {
        val playerName = "Erling Haaland"
        val seasonPerformance1 = PerformanceBuilder()
            .withSeason("2022-23")
            .withTeam("Manchester City")
            .withCompetition("Premier League")
            .withAppearances(35)
            .withGoals(36)
            .withAssists(8)
            .withAerialWons(3.2)
            .withRating(8.8)
            .build()

        val seasonPerformance2 = PerformanceBuilder()
            .withSeason("2023-24")
            .withTeam("Manchester City")
            .withCompetition("Premier League")
            .withAppearances(31)
            .withGoals(27)
            .withAssists(5)
            .withAerialWons(3.0)
            .withRating(8.5)
            .build()

        val playerPerformance = PlayerPerformanceBuilder()
            .withName(playerName)
            .withSeasons(listOf(seasonPerformance1, seasonPerformance2))
            .build()

        every { playerService.findPlayerPerformanceByName(playerName) } returns playerPerformance

        mockMvc.perform(get("/players/{playerName}/performance", playerName)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk,
                content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.name").value(playerName),
                jsonPath("$.seasons").isArray,
                jsonPath("$.seasons[0].season").value("2022-23"),
                jsonPath("$.seasons[0].goals").value(36),
                jsonPath("$.seasons[1].season").value("2023-24"),
                jsonPath("$.seasons[1].goals").value(27)
            )
    }
}