package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.AdjustmentThingsToDo

@Service
class AdjustmentsApiClient(@Qualifier("adjustmentsApiWebClient") private val webClient: WebClient) {
  private val log = LoggerFactory.getLogger(this::class.java)
  private inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}

  fun thingsToDo(prisonerId: String): AdjustmentThingsToDo {
    log.info("Get things to do from Adjustments for $prisonerId")
    //  TODO Remove active case load header, at the moment it's mandatory by the api (but the api doesnt use it). once removed from adjustments can remove from here too
    return webClient.get()
      .uri("/things-to-do/prisoner/$prisonerId")
      .headers { headers ->
        headers.set("Active-Caseload", "COURT_CASES_RELEASE_DATES_API")
      }
      .retrieve()
      .bodyToMono(typeReference<AdjustmentThingsToDo>())
      .block()!!
  }
}
