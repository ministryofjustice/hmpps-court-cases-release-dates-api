package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.AdjustmentsApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2.AdjustmentThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2.EmptyThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.v2.ThingToDo

@Component
class AdjustmentsThingsToDoProvider(
  private val adjustmentsApiClient: AdjustmentsApiClient,
) : ThingsToDoProvider {
  override val serviceName: String = "adjustments"

  override fun getThingToDo(prisonerId: String, existingThingsToDo: MutableList<ThingToDo>): ThingToDo {
    val adjustmentTodos = adjustmentsApiClient.thingsToDo(prisonerId)
    if (adjustmentTodos.thingsToDo.isNotEmpty()) {
      return AdjustmentThingToDo(
        count = 1,
        adaIntercept = adjustmentTodos.adaIntercept,
        types = adjustmentTodos.thingsToDo,
      )
    }
    return EmptyThingToDo()
  }
}
