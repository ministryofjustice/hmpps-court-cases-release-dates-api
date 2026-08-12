package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.RemandAndSentencingThingsToDo

@Service
class RemandAndSentencingApiClient(
  @param:Qualifier("remandAndSentencingApiWebClient") private val webClient: WebClient,
) {
  private val log = LoggerFactory.getLogger(this::class.java)

  private inline fun <reified T : Any> typeReference() = object : ParameterizedTypeReference<T>() {}

  fun thingsToDo(prisonerId: String): RemandAndSentencingThingsToDo {
    log.info("Get things to do from Remand and Sentencing API for $prisonerId")
    return webClient.get()
      .uri("/things-to-do/prisoner/$prisonerId")
      .retrieve()
      .bodyToMono(typeReference<RemandAndSentencingThingsToDo>())
      .block()!!
  }

  fun isImmigrationDetentionPrisoner(prisonerId: String): Boolean {
    log.debug("Check if prisoner {} is an immigration detention prisoner", prisonerId)

    return webClient.get()
      .uri("/immigration-detention/person/$prisonerId/exists")
      .retrieve()
      .bodyToMono(Boolean::class.java)
      .block()!!
  }
}
