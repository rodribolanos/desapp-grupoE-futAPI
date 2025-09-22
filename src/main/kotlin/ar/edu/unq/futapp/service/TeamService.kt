package ar.edu.unq.futapp.service

import ar.edu.unq.futapp.exception.EntityNotFound
import ar.edu.unq.futapp.model.Player
import ar.edu.unq.futapp.model.Team
import lombok.AllArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional

@Service
@AllArgsConstructor
class TeamService @Autowired constructor(val teamProxyService: WhoScoredTeamProxyService) {

    fun findTeamPlayers(teamName: String): List<Player> {
        val team: Optional<Team> = teamProxyService.findTeam(teamName)
        if(team.isEmpty) throw EntityNotFound("Team with name $teamName not found")
        return team.get().players
    }
}