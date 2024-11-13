package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.AdjustmentsApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.CalculateReleaseDatesApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo

@Service
class ThingsToDoService(
  private val calculateReleaseDatesApiClient: CalculateReleaseDatesApiClient,
  private val adjustmentsApiClient: AdjustmentsApiClient,
) {

  // TODO This is a placeholder at the moment, the actual rules are yet to be decided, wil revisit after discussion with analyst/designer (separate ticket)
  fun getToDoList(prisonerId: String): ThingsToDo {
    val adjustmentTodos = adjustmentsApiClient.thingsToDo(prisonerId)
    if (adjustmentTodos.thingsToDo.isNotEmpty()) {
      return adjustmentTodos
    }
    return calculateReleaseDatesApiClient.thingsToDo(prisonerId)
  }
}
