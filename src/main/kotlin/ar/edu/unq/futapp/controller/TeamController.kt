package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.dto.*
import ar.edu.unq.futapp.model.AdvancedMetric
import ar.edu.unq.futapp.model.ProcessStatus
import ar.edu.unq.futapp.service.TaskService
import ar.edu.unq.futapp.service.TeamService
import ar.edu.unq.futapp.service.impl.TaskServiceImpl
import ar.edu.unq.futapp.service.impl.TeamServiceImpl
import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.web.bind.annotation.PostMapping

@Validated
@RestController
@RequestMapping("/teams")
class TeamController {
    @Autowired
    private lateinit var teamService: TeamService
    @Autowired
    private lateinit var taskService: TaskService
    @Autowired
    private lateinit var objectMapper: ObjectMapper

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


    @Operation(summary = "Get advanced metrics for a team in a country",
        description = "Returns advanced statistics for the specified team within the specified country.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved advanced metrics",
                content = [Content(schema = Schema(implementation = AdvancedMetric::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Team or country not found",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Parsing exception",
              content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            )
        ]
    )
    @GetMapping("/{country}/{teamName}/statistics")
    fun advancedMetricsForTeamInCountry(
        @PathVariable("country")
        @NotBlank(message = "country must not be empty or null")
        country: String,
        @PathVariable("teamName")
        @NotBlank(message = "teamName must not be empty or null")
        teamName: String
    ): ResponseEntity<AdvancedMetricDTO> {
        val stats = teamService.getAdvancedMetricsForTeamAndCountry(teamName, country)
        return ResponseEntity.ok(stats.toDTO())
    }
    
    @Operation(summary = "Get comparison process status",
        description = "Returns the status of a team comparison process by its ID.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved comparison process status",
                content = [Content(schema = Schema(implementation = ProcessStatusDTO::class))]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Process not found",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            )
        ]
    )
 
    @GetMapping("/compare/{id}/status")
    fun getCompareStatus(
        @PathVariable("id")
        @NotBlank(message = "id must not be empty or null")
        id: String
    ): ResponseEntity<ProcessStatusDTO> {
        val status: ProcessStatus = taskService.getProcessStatusById(id)
        return ResponseEntity.ok(status.toDTO(objectMapper ))
    }


    @PostMapping("/compare/{team1}/{team2}")
    fun compareTeams(
        @PathVariable("team1")
        @NotBlank(message = "team1 must not be empty or null")
        team1: String,
        @PathVariable("team2")
        @NotBlank(message = "team2 must not be empty or null")
        team2: String
    ): ResponseEntity<ProcessStatusDTO> {
        val processStatus: ProcessStatus = taskService.startTeamComparisonTask(team1, team2)
        return ResponseEntity.accepted().body(processStatus.toDTO(objectMapper))
    }
}
