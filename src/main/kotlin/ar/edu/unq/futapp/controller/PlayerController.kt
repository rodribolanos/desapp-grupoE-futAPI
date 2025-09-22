package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.dto.PlayerDTO
import ar.edu.unq.futapp.dto.toDTO
import ar.edu.unq.futapp.service.TeamService
import lombok.AllArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/teams")
class PlayerController {
    @Autowired
    private lateinit var teamService: TeamService

    @GetMapping("/{teamName}/players")
    fun playersFromTeam(@PathVariable("teamName") teamName: String): ResponseEntity<List<PlayerDTO>> {
        return ResponseEntity.ok(teamService.findTeamPlayers(teamName).toDTO())
    }
}
