package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.CalculateReleaseDatesApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.ManageOffencesApiClient

@Service
class ThingsToDoService(
  private val calculateReleaseDatesApiClient: CalculateReleaseDatesApiClient,
  @Qualifier("manageOffencesApiWebClient") private val manageOffencesApiClient: WebClient,
) {

  // TODO This is a placeholder at the moment, the actual return object will contain more info, wil revisit after discussion with analyst/designer
  fun getToDoList(prisonerId: String): String {
    manageOffencesApiClient.get()
      .uri("/offences/ho-code/COML025")
      .retrieve()
      .bodyToMono(String::class.java)
      .block()!!

    println("Called MO")


    val a = calculateReleaseDatesApiClient.thingsToDo(prisonerId)
    return a
  }
//    val ada = additionalDaysAwardedService.getAdaAdjudicationDetails(prisonerId)
//    val thingsToDo = if (ada.intercept.type != NONE) {
//      listOf(ADA_INTERCEPT)
//    } else {
//      emptyList()
//    }
//
//    return ThingsToDo(
//      prisonerId = prisonerId,
//      thingsToDo = thingsToDo,
//    )
//  }
}
