package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.CalculateReleaseDatesApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2.CalculationThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2.EmptyThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2.ThingToDo

@Component
class CrdsThingsToDoProvider(
  private val calculateReleaseDatesApiClient: CalculateReleaseDatesApiClient,
) : ThingsToDoProvider {
  override val serviceName: String = "releaseDates"

  override fun getThingToDo(prisonerId: String, existingThingsToDo: MutableList<ThingToDo>): ThingToDo {
    if (existingThingsToDo.none { it.count > 0 }) {
      val calculationThingsToDo = calculateReleaseDatesApiClient.thingsToDo(prisonerId)
      if (calculationThingsToDo.thingsToDo.isNotEmpty()) {
        return CalculationThingToDo(
          count = 1,
          type = calculationThingsToDo.thingsToDo.first(),
        )
      }
    }
    return EmptyThingToDo()
  }
}
