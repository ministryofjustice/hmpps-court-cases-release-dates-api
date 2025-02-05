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
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service.ThingToDoCacheService

@RestController
@RequestMapping("/things-to-do", produces = [MediaType.APPLICATION_JSON_VALUE])
@Tag(name = "ThingsToDoController", description = "Operations related to the things-to-do list for prisoners")
class ThingsToDoResource(
  private val thingToDoCacheService: ThingToDoCacheService,
) {
  @DeleteMapping("/prisoner/{prisonerId}/evict")
  @PreAuthorize("hasAnyRole('COURT_CASES_RELEASE_DATES__PRE_SENTENCE_CALC_REVIEW_TASKS__RW')")
  @Operation(
    summary = "Evict the cached things to do",
    description = "Evict the cached things to do when underlying data has changed.",
    security = [SecurityRequirement(name = "court-cases-release-dates-api-things-to-do-rw-role")],
  )
  @ApiResponses(
    value = [
      ApiResponse(responseCode = "200", description = "Successfuly evicted cache."),
      ApiResponse(responseCode = "401", description = "Unauthorized - valid Oauth2 token required"),
      ApiResponse(responseCode = "403", description = "Forbidden - requires appropriate role"),
    ],
  )
  fun evictThingsToDo(
    @Parameter(required = true, example = "A1234AB", description = "Prisoner's ID (also known as nomsId)")
    @PathVariable prisonerId: String,
  ) {
    log.info("Request to evict thing to do cache for prisoner ID: {}", prisonerId)
    ThingToDoType.entries.forEach {
      thingToDoCacheService.evictCache(it.name, prisonerId)
    }
  }

  companion object {
    private val log: Logger = LoggerFactory.getLogger(ThingsToDoResource::class.java)
  }
}
