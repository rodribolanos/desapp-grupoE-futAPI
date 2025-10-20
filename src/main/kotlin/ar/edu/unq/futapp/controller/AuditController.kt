package ar.edu.unq.futapp.controller

import ar.edu.unq.futapp.dto.ExceptionDTO
import ar.edu.unq.futapp.dto.PageResponseDTO
import ar.edu.unq.futapp.dto.RequestAuditDTO
import ar.edu.unq.futapp.dto.toDTO
import ar.edu.unq.futapp.dto.toPageDTO
import ar.edu.unq.futapp.service.AuditService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/me")
class AuditController @Autowired constructor(
    private val auditService: AuditService
) {

    @Operation(
        summary = "Authenticated user's request history",
        description = "Returns the user's paginated history, from most recent to oldest.",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "History retrieved",
                content = [Content(schema = Schema(implementation = RequestAuditDTO::class))]
            ),
            ApiResponse(
                responseCode = "401",
                description = "Unauthorized",
                content = [Content(schema = Schema(implementation = ExceptionDTO::class))]
            )
        ]
    )
    @GetMapping("/history")
    fun myHistory(
        @RequestParam(name = "page", defaultValue = "0") @PositiveOrZero page: Int,
        @RequestParam(name = "size", defaultValue = "20") @Positive size: Int
    ): ResponseEntity<PageResponseDTO<RequestAuditDTO>> {
        return ResponseEntity.ok(auditService.myHistory(page, size).toPageDTO { it.toDTO() })
    }
}
