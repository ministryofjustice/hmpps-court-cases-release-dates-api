package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.CalculateReleaseDatesApiClient

@Service
class ThingsToDoService(
  private val calculateReleaseDatesApiClient: CalculateReleaseDatesApiClient,
) {

  // TODO This is a placeholder at the moment, the actual return object will contain more info, wil revisit after discussion with analyst/designer
  fun getToDoList(prisonerId: String): String {
    val a = calculateReleaseDatesApiClient.thingsToDo(prisonerId)
    return a
  }
}