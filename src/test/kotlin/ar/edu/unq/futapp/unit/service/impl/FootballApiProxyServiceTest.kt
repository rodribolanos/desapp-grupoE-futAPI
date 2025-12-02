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
}