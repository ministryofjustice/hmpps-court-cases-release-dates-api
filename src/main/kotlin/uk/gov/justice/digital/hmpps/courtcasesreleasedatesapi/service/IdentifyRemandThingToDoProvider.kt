package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.IdentifyRemandApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.CacheableThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo

@Component
class IdentifyRemandThingToDoProvider(
  private val identifyRemandApiClient: IdentifyRemandApiClient,
  @Value("\${hmpps-identify-remand-ui.url}") private val identifyRemandApiBaseUri: String,

) : ThingsToDoProvider {
  override val serviceName: String = "adjustments"

  override fun getThingToDo(
    prisonerId: String,
    existingThingsToDo: MutableList<ThingsToDo>,
    serviceConfig: CcrdServiceConfig,
  ): CacheableThingToDo {
    val thingsToDo = identifyRemandApiClient.thingsToDo(prisonerId)
    if (thingsToDo.thingsToDo.isNotEmpty()) {
      var title = "The remand tool has calculated that there is no remand to be applied."
      thingsToDo.days?.let { days ->
        if (days > 0) {
          title = "The remand tool has calculated that there is relevant remand to be applied."
        }
      }
      return CacheableThingToDo(
        ThingToDo(
          title = title,
          message = "Review the remand tool before calculating a release date.",
          buttonText = "Review remand",
          buttonHref = "$identifyRemandApiBaseUri/prisoner/$prisonerId",
          type = ThingToDoType.REVIEW_IDENTIFIED_REMAND,
        ),
      )
    }
    return CacheableThingToDo()
  }

  override fun additionalRoles(): List<String> = listOf(IDENTIFY_REMAND_ROLE)

  override fun thingToDoType(): ThingToDoType = ThingToDoType.REVIEW_IDENTIFIED_REMAND

  companion object {
    private const val IDENTIFY_REMAND_ROLE = "REMAND_IDENTIFIER"
  }
}
