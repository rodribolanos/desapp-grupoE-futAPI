package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.Match
import ar.edu.unq.futapp.model.PlayerPerformance
import ar.edu.unq.futapp.model.Team
import ar.edu.unq.futapp.model.UpcomingMatch
import java.util.Optional

interface WhoScoredApiClient {
    fun findTeam(teamName: String): Optional<Team>
    fun findPlayerPerformance(playerName: String): Optional<PlayerPerformance>
    fun findUpcomingFixtures(teamName: String): Optional<List<UpcomingMatch>>
    fun findLastMatchesFromTeam(teamName: String): List<Match>
}
