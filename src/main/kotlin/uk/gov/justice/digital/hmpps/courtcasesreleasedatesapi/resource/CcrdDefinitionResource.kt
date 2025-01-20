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
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.CcrdServiceDefinitions
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service.CcrdDefinitionService

@RestController
@RequestMapping("/service-definitions", produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "ThingsToDoController", description = "Operations related to the things-to-do list for prisoners")
class CcrdDefinitionResource(
  private val ccrdDefinitionService: CcrdDefinitionService,
) {
  @GetMapping("/prisoner/{prisonerId}")
  @PreAuthorize("hasAnyRole('RELEASE_DATES_CALCULATOR')")
  @Operation(
    summary = "Retrieve the configuration of the CCRD services for a given prisoner",
    description = "Provides a list of services, their configuration and things-to-do for a specified prisoner.",
    security = [SecurityRequirement(name = "user-token")],
  )
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Successfully returns the ccrd service list"),
      ApiResponse(responseCode = "401", description = "Unauthorized - valid Oauth2 token required"),
      ApiResponse(responseCode = "403", description = "Forbidden - requires appropriate role"),
    ],
  )
  fun getCcrdConfiguration(
    @Parameter(required = true, example = "A1234AB", description = "Prisoner's ID (also known as nomsId)")
    @PathVariable prisonerId: String,
  ): CcrdServiceDefinitions {
    log.info("Request to retrieve ccrd service list for prisoner ID: {}", prisonerId)
    return ccrdDefinitionService.getCcrdConfiguration(prisonerId)
  }

  companion object {
    private val log: Logger = LoggerFactory.getLogger(CcrdDefinitionResource::class.java)
  }
}
