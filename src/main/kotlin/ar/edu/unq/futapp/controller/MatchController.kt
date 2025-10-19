package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.dto.MatchPredictionDTO
import ar.edu.unq.futapp.dto.toDTO
import ar.edu.unq.futapp.service.MatchService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/match")
class MatchController(
    @Autowired private val matchService: MatchService
) {

    @Operation(summary = "Predict the outcome of a match between two teams")
    @ApiResponses
    (
        value = [
            ApiResponse(responseCode = "200", description = "Match prediction retrieved successfully"),
            ApiResponse(responseCode = "404", description = "One or both teams not found"),
            ApiResponse(responseCode = "500", description = "Internal server error")
        ]
    )
    @GetMapping("/{homeTeam}/{awayTeam}/prediction")
    fun predictMatch(@PathVariable homeTeam: String, @PathVariable awayTeam: String): ResponseEntity<MatchPredictionDTO> {
        var matchPrediction = matchService.predictMatch(homeTeam, awayTeam)
        return ResponseEntity.ok(matchPrediction.toDTO())
    }

}