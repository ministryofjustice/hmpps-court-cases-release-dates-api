package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service.ThingsToDoService

@RestController
@RequestMapping("/things-to-do", produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "ThingsToDoController", description = "Operations related to the things-to-do list for prisoners")
class ThingsToDoResource(
  private val thingsToDoService: ThingsToDoService,
) {
  @GetMapping("/prisoner/{prisonerId}/{activeCaseLoadId}")
  @PreAuthorize("hasAnyRole('ADJUSTMENTS__ADJUSTMENTS_RO', 'RELEASE_DATES_CALCULATOR')")
  @Operation(
    summary = "Retrieve things-to-do for a prisoner",
    description = "Provides a list of things-to-do for a specified prisoner based on their ID.",
    security = [SecurityRequirement(name = "court-cases-release-dates-api-ui-role")],
  )
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Successfully returns the things-to-do list"),
      ApiResponse(responseCode = "401", description = "Unauthorized - valid Oauth2 token required"),
      ApiResponse(responseCode = "403", description = "Forbidden - requires appropriate role"),
    ],
  )
  fun getThingsToDo(
    @Parameter(required = true, example = "A1234AB", description = "Prisoner's ID (also known as nomsId)")
    @PathVariable prisonerId: String,
    @Parameter(required = true, example = "MDI", description = "Active caseload ID (required for adjustments call)")
    @PathVariable activeCaseLoadId: String,
  ): ThingsToDo {
    log.info("Request to retrieve things-to-do list for prisoner ID: {} for caseload {}", prisonerId, activeCaseLoadId)
    return thingsToDoService.getToDoList(prisonerId, activeCaseLoadId)
  }

  companion object {
    private val log: Logger = LoggerFactory.getLogger(ThingsToDoResource::class.java)
  }
}
