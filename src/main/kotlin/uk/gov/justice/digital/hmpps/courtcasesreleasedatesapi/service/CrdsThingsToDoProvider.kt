package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.CalculateReleaseDatesApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2.ThingsToDo

@Component
class CrdsThingsToDoProvider(
  private val calculateReleaseDatesApiClient: CalculateReleaseDatesApiClient,
) : ThingsToDoProvider {
  override val serviceName: String = "releaseDates"

  override fun getThingToDo(prisonerId: String, existingThingsToDo: MutableList<ThingsToDo>, serviceConfig: CcrdServiceConfig): ThingsToDo {
    if (existingThingsToDo.none { it.count > 0 }) {
      val calculationThingsToDo = calculateReleaseDatesApiClient.thingsToDo(prisonerId)
      if (calculationThingsToDo.thingsToDo.isNotEmpty()) {
        return ThingsToDo(
          things = listOf(
            ThingToDo(
              title = "Calculation required",
              message = "Some information has changed. Check that all information is up to date then calculate release dates.",
              buttonText = "Calculate release dates",
              buttonHref = serviceConfig.uiUrl + "/calculation/" + prisonerId + "/reason",
              type = ThingToDoType.CALCULATION_REQUIRED,
            ),
          ),

        )
      }
    }
    return ThingsToDo(emptyList())
  }
}
