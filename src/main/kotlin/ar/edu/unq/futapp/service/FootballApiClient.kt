package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.AdvancedMetric

interface FootballApiClient {
    fun getIdForTeam(teamName: String): Int
    fun getIdForCountryLeague(countryName: String): Int
    fun getAdvancedMetricsForTeamAndCountry(teamId: Int, leagueId: Int): AdvancedMetric
}