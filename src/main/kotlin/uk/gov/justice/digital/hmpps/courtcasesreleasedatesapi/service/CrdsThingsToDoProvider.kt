package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.CalculateReleaseDatesApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.CalculationToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDoProviderName

@Component
class CrdsThingsToDoProvider(
  private val calculateReleaseDatesApiClient: CalculateReleaseDatesApiClient,
) : ThingsToDoProvider {
  override val serviceName: String = "releaseDates"

  override fun getThingsToDo(
    prisonerId: String,
    existingThingsToDo: MutableList<ThingsToDo>,
    serviceConfig: CcrdServiceConfig,
  ): List<ThingToDo> {
    if (existingThingsToDo.none { it.count > 0 }) {
      val calculationThingsToDo = calculateReleaseDatesApiClient.thingsToDo(prisonerId)
      return calculationThingsToDo.thingsToDo.map { type ->
        when (type) {
          CalculationToDoType.CALCULATION_REQUIRED -> ThingToDo(
            title = "Calculation required",
            message = "Some information has changed. Check that all information is up to date then calculate release dates.",
            buttonText = "Calculate release dates",
            buttonHref = "${serviceConfig.uiUrl}/calculation/$prisonerId/reason",
            type = ThingToDoType.CALCULATION_REQUIRED,
          )
        }
      }
    }
    return emptyList()
  }

  override fun thingsToDoProviderName(): ThingsToDoProviderName = ThingsToDoProviderName.CRDS
}
