package ar.edu.unq.futapp.service.impl

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.Team
import ar.edu.unq.futapp.service.TeamApiClient
import ar.edu.unq.futapp.service.TeamService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class TeamServiceImpl @Autowired constructor(val teamApiClient: TeamApiClient): TeamService {
    override fun findPlayersByTeam(teamName: String): List<Player> {
        val team: Optional<Team> = teamApiClient.findTeam(teamName)
        if(team.isEmpty) throw EntityNotFound("Team with name $teamName not found")
        return team.get().players
    }
}