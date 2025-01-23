package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.IdentifyRemandApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo
import uk.gov.justice.hmpps.kotlin.auth.HmppsAuthenticationHolder

@Component
class IdentifyRemandThingToDoProvider(
  private val identifyRemandApiClient: IdentifyRemandApiClient,
  @Value("\${hmpps-identify-remand-ui.url}") private val identifyRemandApiBaseUri: String,

) : ThingsToDoProvider {
  override val serviceName: String = "adjustments"

  override fun getThingToDo(prisonerId: String, existingThingsToDo: MutableList<ThingsToDo>, serviceConfig: CcrdServiceConfig): ThingToDo? {
    if (HmppsAuthenticationHolder.hasRoles(IDENTIFY_REMAND_ROLE)) {
      val thingsToDo = identifyRemandApiClient.thingsToDo(prisonerId)
      if (thingsToDo.thingsToDo.isNotEmpty()) {
        return ThingToDo(
          title = "There are periods of remand to review",
          message = "This service has identified periods of remand that may be relevant. You must review this remand periods before calculating a release date.",
          buttonText = "Review remand",
          buttonHref = "$identifyRemandApiBaseUri/prisoner/$prisonerId",
          type = ThingToDoType.REVIEW_IDENTIFIED_REMAND,
        )
      }
    }
    return null
  }

  companion object {
    private const val IDENTIFY_REMAND_ROLE = "REMAND_IDENTIFIER"
  }
}
