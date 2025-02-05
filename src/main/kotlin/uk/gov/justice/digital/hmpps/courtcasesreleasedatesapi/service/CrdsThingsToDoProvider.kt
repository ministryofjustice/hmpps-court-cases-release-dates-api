package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.CalculateReleaseDatesApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.CacheableThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo

@Component
class CrdsThingsToDoProvider(
  private val calculateReleaseDatesApiClient: CalculateReleaseDatesApiClient,
) : ThingsToDoProvider {
  override val serviceName: String = "releaseDates"

  override fun getThingToDo(prisonerId: String, existingThingsToDo: MutableList<ThingsToDo>, serviceConfig: CcrdServiceConfig): CacheableThingToDo {
    if (existingThingsToDo.none { it.count > 0 }) {
      val calculationThingsToDo = calculateReleaseDatesApiClient.thingsToDo(prisonerId)
      if (calculationThingsToDo.thingsToDo.isNotEmpty()) {
        return CacheableThingToDo(
          ThingToDo(
            title = "Calculation required",
            message = "Some information has changed. Check that all information is up to date then calculate release dates.",
            buttonText = "Calculate release dates",
            buttonHref = serviceConfig.uiUrl + "/calculation/" + prisonerId + "/reason",
            type = ThingToDoType.CALCULATION_REQUIRED,
          ),
        )
      }
    }
    return CacheableThingToDo()
  }
  override fun thingToDoType(): ThingToDoType = ThingToDoType.CALCULATION_REQUIRED
}
