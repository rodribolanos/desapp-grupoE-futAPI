package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.dto.PlayerPerformanceDTO
import ar.edu.unq.futapp.dto.toDTO
import ar.edu.unq.futapp.service.PlayerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.constraints.NotBlank
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/players")
@Validated
class PlayerController {
    @Autowired
    lateinit var playerService: PlayerService

    @Operation(summary = "Get player performance by name",
        description = "Returns the performance statistics of a player for all seasons.",
        security = [SecurityRequirement(name = "bearerAuth")]
        )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successfully retrieved player performance"
            ),
            ApiResponse(
                responseCode = "404",
                description = "Player not found"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Internal server error"
            )
        ]
    )
    @GetMapping("{playerName}/performance")
    fun getPlayerPerformance(
        @PathVariable("playerName")
        @NotBlank(message = "playerName must not be empty or null")
        playerName: String): ResponseEntity<PlayerPerformanceDTO> {
        return ResponseEntity.ok(playerService.findPlayerPerformanceByName(playerName).toDTO())
    }
}