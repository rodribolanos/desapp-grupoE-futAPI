package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.*
import java.util.Optional

interface WhoScoredApiClient {
    fun findTeam(teamName: String): Optional<Team>
    fun findPlayerPerformance(playerName: String): Optional<PlayerPerformance>
    fun findUpcomingFixtures(teamName: String): Optional<List<UpcomingMatch>>
    fun findLastMatchesFromTeam(teamName: String): TeamMatches
    fun findTeamMetrics(team: String): TeamMetrics
}
