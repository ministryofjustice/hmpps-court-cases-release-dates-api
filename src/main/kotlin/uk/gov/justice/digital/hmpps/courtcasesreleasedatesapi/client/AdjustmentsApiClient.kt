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

  fun thingsToDo(prisonerId: String, activeCaseLoadId: String): AdjustmentThingsToDo {
    log.info("Get things to do from Adjustments for $prisonerId")
    return webClient.get()
      .uri("/things-to-do/prisoner/$prisonerId")
      .headers { headers ->
        headers.set("Active-Caseload", activeCaseLoadId)
      }
      .retrieve()
      .bodyToMono(typeReference<AdjustmentThingsToDo>())
      .block()!!
  }
}
