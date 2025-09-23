package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.model.Team
import java.util.Optional

interface TeamApiClient {
    fun findTeam(teamName: String): Optional<Team>
}
