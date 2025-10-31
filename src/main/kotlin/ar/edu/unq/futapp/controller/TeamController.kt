package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.dto.AuthResponseDTO
import ar.edu.unq.futapp.dto.ExceptionDTO
import ar.edu.unq.futapp.dto.PlayerDTO
import ar.edu.unq.futapp.dto.UpcomingMatchDTO
import ar.edu.unq.futapp.dto.toDTO
import ar.edu.unq.futapp.service.impl.TeamServiceImpl
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import io.swagger.v3.oas.annotations.security.SecurityRequirement

@Validated
@RestController
@RequestMapping("/teams")
class TeamController {
    @Autowired
    private lateinit var teamService: TeamServiceImpl

    @Operation(summary = "Get players from a team",
        description = "Returns a list of players for the specified team.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved players",
                content = [Content(schema = Schema(implementation = AuthResponseDTO::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Team not found",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Parsing exception",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            )
        ]
    )
    @GetMapping("/{teamName}/players")
    fun playersFromTeam(
        @PathVariable("teamName")
        @NotBlank(message = "teamName must not be empty or null")
        teamName: String
    ): ResponseEntity<List<PlayerDTO>> {
        return ResponseEntity.ok(teamService.findPlayersByTeam(teamName).toDTO())
    }

    @Operation(summary = "Get upcoming fixtures for a team",
        description = "Returns upcoming (not yet played) fixtures for the specified team.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved upcoming fixtures",
                content = [Content(schema = Schema(implementation = UpcomingMatchDTO::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Team not found",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Parsing exception",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            )
        ]
    )
    @GetMapping("/{teamName}/fixtures/upcoming")
    fun upcomingFixtures(
        @PathVariable("teamName")
        @NotBlank(message = "teamName must not be empty or null")
        teamName: String
    ): ResponseEntity<List<UpcomingMatchDTO>> {
        val matches = teamService.findUpcomingFixturesByTeam(teamName)
        return ResponseEntity.ok(matches.toDTO())
    }
}
