package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.ImmigrationDetention
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

  fun findLatestImmigrationDetentionRecordByPerson(prisonerId: String): ImmigrationDetention? {
    log.info("Get latest immigration detention record from Remand and Sentencing API for $prisonerId")

    return try {
      webClient.get()
        .uri("/immigration-detention/person/$prisonerId/latest")
        .retrieve()
        .bodyToMono(typeReference<ImmigrationDetention>())
        .block()
    } catch (e: WebClientResponseException.NotFound) {
      log.info("No immigration detention record found for prisoner $prisonerId")
      null
    }
  }
}
