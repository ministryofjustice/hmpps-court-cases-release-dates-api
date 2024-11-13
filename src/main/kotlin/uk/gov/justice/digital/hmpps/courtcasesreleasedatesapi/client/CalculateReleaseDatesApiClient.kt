package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service.ThingsToDo

@Service
class CalculateReleaseDatesApiClient(@Qualifier("calculateReleaseDatesApiWebClient") private val webClient: WebClient) {
  private val log = LoggerFactory.getLogger(this::class.java)
  private inline fun <reified T> typeReference() = object : ParameterizedTypeReference<T>() {}

  fun thingsToDo(prisonerId: String): ThingsToDo {
    log.info("Get things to do from CRD for $prisonerId")
    return webClient.get()
      .uri("/things-to-do/prisoner/$prisonerId")
      .retrieve()
      .bodyToMono(typeReference<ThingsToDo>())
      .block()!!
  }
}
