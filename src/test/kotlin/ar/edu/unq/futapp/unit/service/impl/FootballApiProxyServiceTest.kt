package ar.edu.unq.futapp.unit.service.impl

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.service.footballApiResponse.*
import ar.edu.unq.futapp.service.impl.FootballApiProxyService
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

class FootballApiProxyServiceTest {
    @Mock
    private lateinit var restTemplate: RestTemplate
    @Mock
    private lateinit var objectMapper: ObjectMapper

    private lateinit var service: FootballApiProxyService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        service = FootballApiProxyService(restTemplate, objectMapper)
        ReflectionTestUtils.setField(service, "baseUrl", "https://api.test.com")
        ReflectionTestUtils.setField(service, "apiKey", "test-api-key")
    }

    @Test
    @DisplayName("when team exists in API response then returns team ID")
    fun whenTeamExistsInApiResponse_thenReturnsTeamId() {
        // Given
        val teamName = "Manchester United"
        val responseBody = """{"response":[{"team":{"id":33,"name":"Manchester United"}}],"results":1}"""
        val expectedResponse = TeamApiResponse(
            response = listOf(TeamResponseItem(TeamInfo(33, "Manchester United", "MUN", "England"))),
            results = 1
        )

        whenever(restTemplate.exchange(
            eq("https://api.test.com/teams?name=$teamName"),
            eq(HttpMethod.GET),
            any<HttpEntity<String>>(),
            eq(String::class.java)
        )).thenReturn(ResponseEntity.ok(responseBody))

        whenever(objectMapper.readValue(responseBody, TeamApiResponse::class.java))
            .thenReturn(expectedResponse)

        val result = service.getIdForTeam(teamName)

        assertEquals(33, result)
    }

    @Test
    @DisplayName("when team does not exist in API response then throws EntityNotFound")
    fun whenTeamDoesNotExistInApiResponse_thenThrowsEntityNotFound() {
        val teamName = "Pepitos team"
        val responseBody = """{"response":[],"results":0}"""
        val expectedResponse = TeamApiResponse(response = emptyList(), results = 0)

        whenever(restTemplate.exchange(
            eq("https://api.test.com/teams?name=$teamName"),
            eq(HttpMethod.GET),
            any<HttpEntity<String>>(),
            eq(String::class.java)
        )).thenReturn(ResponseEntity.ok(responseBody))

        whenever(objectMapper.readValue(responseBody, TeamApiResponse::class.java))
            .thenReturn(expectedResponse)

        val exception = assertThrows<EntityNotFound> {
            service.getIdForTeam(teamName)
        }
        assertEquals("Team with name 'Pepitos team' not found.", exception.message)
    }

    @Test
    @DisplayName("when country league exists in API response then returns league ID")
    fun whenCountryLeagueExistsInApiResponse_thenReturnsLeagueId() {
        // Given
        val countryName = "England"
        val responseBody = """{"response":[{"league":{"id":39,"name":"Premier League","type":"league","logo":"logo.png"}}],"results":1}"""
        val expectedResponse = LeagueApiResponse(
            response = listOf(LeagueResponseItem(LeagueInfo(39, "Premier League", "league", "logo.png"))),
            results = 1
        )

        whenever(restTemplate.exchange(
            eq("https://api.test.com/leagues?country=$countryName&type=league"),
            eq(HttpMethod.GET),
            any<HttpEntity<String>>(),
            eq(String::class.java)
        )).thenReturn(ResponseEntity.ok(responseBody))

        whenever(objectMapper.readValue(responseBody, LeagueApiResponse::class.java))
            .thenReturn(expectedResponse)

        val result = service.getIdForCountryLeague(countryName)

        assertEquals(39, result)
    }

    @Test
    @DisplayName("when country league does not exist in API response then throws EntityNotFound")
    fun whenCountryLeagueDoesNotExistInApiResponse_thenThrowsEntityNotFound() {
        val countryName = "Pais Inexistente"
        val responseBody = """{"response":[],"results":0}"""
        val expectedResponse = LeagueApiResponse(response = emptyList(), results = 0)

        whenever(restTemplate.exchange(
            eq("https://api.test.com/leagues?country=$countryName&type=league"),
            eq(HttpMethod.GET),
            any<HttpEntity<String>>(),
            eq(String::class.java)
        )).thenReturn(ResponseEntity.ok(responseBody))

        whenever(objectMapper.readValue(responseBody, LeagueApiResponse::class.java))
            .thenReturn(expectedResponse)

        val exception = assertThrows<EntityNotFound> {
            service.getIdForCountryLeague(countryName)
        }
        assertEquals("Country with name 'Pais Inexistente' not found.", exception.message)
    }
//
//    @Test
//    @DisplayName("when team statistics exist in API response then returns advanced metrics")
//    fun whenTeamStatisticsExistInApiResponse_thenReturnsAdvancedMetrics() {
//        val teamId = 33
//        val leagueId = 39
//        val responseBody = """{"response":{"team":{"id":33,"name":"Manchester United"}},"results":1}"""
//
//        val mockLineups = listOf(
//            LineupInfo("4-2-3-1", 32),
//            LineupInfo("4-3-3", 6)
//        )
//
//        val mockGoals = GoalsStatistics(
//            `for` = GoalDirection(mapOf(
//                "76-90" to PeriodStatistic(14, "24.14%"),
//                "61-75" to PeriodStatistic(10, "17.24%")
//            )),
//            against = GoalDirection(mapOf(
//                "16-30" to PeriodStatistic(12, "21.05%"),
//                "61-75" to PeriodStatistic(9, "15.79%")
//            ))
//        )
//
//        val mockCards = CardsStatistics(
//            yellow = mapOf(
//                "91-105" to PeriodStatistic(19, "22.89%"),
//                "76-90" to PeriodStatistic(16, "19.28%")
//            )
//        )
//
//        val expectedResponse = StatisticsApiResponse(
//            response = TeamStatisticsResponse(
//                team = BasicTeamInfo(33, "Manchester United"),
//                lineups = mockLineups,
//                goals = mockGoals,
//                cards = mockCards
//            ),
//            results = 1
//        )
//
//        whenever(restTemplate.exchange(
//            eq("https://api.test.com/statistics?team=$teamId&league=$leagueId"),
//            eq(HttpMethod.GET),
//            any<HttpEntity<String>>(),
//            eq(String::class.java)
//        )).thenReturn(ResponseEntity.ok(responseBody))
//
//        whenever(objectMapper.readValue(responseBody, StatisticsApiResponse::class.java))
//            .thenReturn(expectedResponse)
//
//        // When
//        val result = service.getAdvancedMetricsForTeamAndCountry(teamId, leagueId)
//
//        // Then
//        assertEquals("Manchester United", result.teamName)
//        assertEquals("4-2-3-1", result.mostPlayedLineUp)
//        assertEquals("91-105", result.highestYellowCardsPeriod)
//        assertEquals("76-90", result.highestGoalsScoredPeriod)
//        assertEquals("16-30", result.highestGoalsConcededPeriod)
//    }
//
//    @Test
//    @DisplayName("when team statistics do not exist in API response then throws EntityNotFound")
//    fun whenTeamStatisticsDoNotExistInApiResponse_thenThrowsEntityNotFound() {
//        val teamId = 999
//        val leagueId = 999
//        val responseBody = """{"response":null,"results":0}"""
//        val expectedResponse = StatisticsApiResponse(
//            response = TeamStatisticsResponse(
//                BasicTeamInfo(0, ""),
//                emptyList(),
//                GoalsStatistics(GoalDirection(emptyMap()), GoalDirection(emptyMap())),
//                CardsStatistics(emptyMap())
//            ),
//            results = 0
//        )
//
//        whenever(restTemplate.exchange(
//            eq("https://api.test.com/statistics?team=$teamId&league=$leagueId"),
//            eq(HttpMethod.GET),
//            any<HttpEntity<String>>(),
//            eq(String::class.java)
//        )).thenReturn(ResponseEntity.ok(responseBody))
//
//        whenever(objectMapper.readValue(responseBody, StatisticsApiResponse::class.java))
//            .thenReturn(expectedResponse)
//
//        // When & Then
//        val exception = assertThrows<EntityNotFound> {
//            service.getAdvancedMetricsForTeamAndCountry(teamId, leagueId)
//        }
//        assertEquals("Statistics for team ID '999' in league ID '999' not found.", exception.message)
//    }
//
//    @Test
//    fun `getAdvancedMetricsForTeamAndCountry - datos parciales - retorna Unknown para campos faltantes`() {
//        // Given
//        val teamId = 33
//        val leagueId = 39
//        val responseBody = """{"response":{"team":{"id":33,"name":"Test Team"}},"results":1}"""
//
//        val expectedResponse = StatisticsApiResponse(
//            response = TeamStatisticsResponse(
//                team = BasicTeamInfo(33, "Test Team"),
//                lineups = emptyList(),
//                goals = GoalsStatistics(
//                    `for` = GoalDirection(emptyMap()),
//                    against = GoalDirection(emptyMap())
//                ),
//                cards = CardsStatistics(emptyMap())
//            ),
//            results = 1
//        )
//
//        whenever(restTemplate.exchange(
//            eq("https://api.test.com/statistics?team=$teamId&league=$leagueId"),
//            eq(HttpMethod.GET),
//            any<HttpEntity<String>>(),
//            eq(String::class.java)
//        )).thenReturn(ResponseEntity.ok(responseBody))
//
//        whenever(objectMapper.readValue(responseBody, StatisticsApiResponse::class.java))
//            .thenReturn(expectedResponse)
//
//        val result = service.getAdvancedMetricsForTeamAndCountry(teamId, leagueId)
//
//        assertEquals("Test Team", result.teamName)
//        assertEquals("Unknown", result.mostPlayedLineUp)
//        assertEquals("Unknown", result.highestYellowCardsPeriod)
//        assertEquals("Unknown", result.highestGoalsScoredPeriod)
//        assertEquals("Unknown", result.highestGoalsConcededPeriod)
//    }
}