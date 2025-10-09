package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.PlayerPerformance
import ar.edu.unq.futapp.model.Team
import java.util.Optional

interface WhoScoredApiClient {
    fun findTeam(teamName: String): Optional<Team>
    fun findPlayerPerformance(playerName: String): Optional<PlayerPerformance>
}
