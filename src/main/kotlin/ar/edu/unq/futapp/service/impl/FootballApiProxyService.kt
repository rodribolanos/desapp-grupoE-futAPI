package ar.edu.unq.futapp.service.impl

import ar.edu.unq.futapp.dto.footballAPI.*
import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.model.AdvancedMetric
import ar.edu.unq.futapp.service.FootballApiClient
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class FootballApiProxyService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper = ObjectMapper()
): FootballApiClient {

    @Value("\${football.api.baseUrl}")
    private lateinit var baseUrl: String

    @Value("\${football.api.api-key}")
    private lateinit var apiKey: String

    private fun createHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.set("x-apisports-key", apiKey)
        return headers
    }

    override fun getIdForTeam(teamName: String): Int {
        val endpoint = "$baseUrl/teams?name=$teamName"
        val headers = HttpEntity<String>(createHeaders())

        val response = restTemplate.exchange(
            endpoint,
            org.springframework.http.HttpMethod.GET,
            headers,
            String::class.java
        )

        if (!response.statusCode.is2xxSuccessful || response.body.isNullOrBlank()) {
            throw EntityNotFound("API returned invalid response for team '$teamName'")
        }

        val teamResponse = objectMapper.readValue(response.body, TeamApiResponse::class.java)

        if (teamResponse.results == 0 || teamResponse.response.isEmpty()) {
            throw EntityNotFound("Team with name '$teamName' not found.")
        }
        return teamResponse.response[0].team.id
    }

    override fun getIdForCountryLeague(countryName: String): Int {
        val endpoint = "$baseUrl/leagues?country=$countryName&type=league"
        val headers = HttpEntity<String>(createHeaders())

        val response = restTemplate.exchange(
            endpoint,
            org.springframework.http.HttpMethod.GET,
            headers,
            String::class.java
        )

        if (!response.statusCode.is2xxSuccessful || response.body.isNullOrBlank()) {
            throw EntityNotFound("API returned invalid response for team '$countryName'")
        }

        val leagueResponse: LeagueApiResponse = objectMapper.readValue(response.body, LeagueApiResponse::class.java)

        if (leagueResponse.results == 0 || leagueResponse.response.isEmpty()) {
            throw EntityNotFound("Country with name '$countryName' not found.")
        }
        return leagueResponse.response[0].league.id
    }

    override fun getAdvancedMetricsForTeamAndCountry(teamId: Int, leagueId: Int): AdvancedMetric {
        val endpoint = "$baseUrl/teams/statistics?team=$teamId&league=$leagueId&season=2023"
        val headers = HttpEntity<String>(createHeaders())

        val response = restTemplate.exchange(
            endpoint,
            org.springframework.http.HttpMethod.GET,
            headers,
            String::class.java
        )

        if (!response.statusCode.is2xxSuccessful || response.body.isNullOrBlank()) {
            throw EntityNotFound("API returned invalid response for team '$teamId' in league '$leagueId'.")
        }

        val statsResponse: StatisticsApiResponse = objectMapper.readValue(response.body, StatisticsApiResponse::class.java)

        if (statsResponse.results == 0 ) {
            throw EntityNotFound("Statistics for team ID '$teamId' in league ID '$leagueId' not found.")
        }

        val stats: TeamStatisticsResponse = statsResponse.response
        val mostUsedLineup = stats.lineups.maxByOrNull { it.played }?.formation ?: "Unknown"

        val peakYellowCardsPeriod = findPeakPeriod(stats.cards.yellow)

        val peakGoalsScoredPeriod = findPeakPeriod(stats.goals.`for`.minute)

        val peakGoalsConcededPeriod = findPeakPeriod(stats.goals.against.minute)

        return AdvancedMetric(
            teamName = stats.team.name,
            mostPlayedLineUp = mostUsedLineup,
            highestYellowCardsPeriod = peakYellowCardsPeriod,
            highestGoalsScoredPeriod = peakGoalsScoredPeriod,
            highestGoalsConcededPeriod = peakGoalsConcededPeriod
        )
    }

    private fun findPeakPeriod(periodMap: Map<String, PeriodStatistic>): String {
        return periodMap
            .filter { it.value.total != null && it.key.isNotEmpty() }
            .maxByOrNull { it.value.total!! }
            ?.key ?: "Unknown"
    }
}