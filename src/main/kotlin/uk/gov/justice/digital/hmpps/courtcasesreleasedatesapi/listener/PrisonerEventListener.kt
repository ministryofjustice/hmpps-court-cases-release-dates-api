package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.listener

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service.ThingToDoCacheService

@Service
class PrisonerEventListener(
  private val objectMapper: ObjectMapper,
  private val thingToDoCacheService: ThingToDoCacheService,
) {

  private companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
    val prisonIdFields = listOf("nomsNumber", "removedNomsNumber", "movedFromNomsNumber", "movedToNomsNumber")
  }

  @SqsListener("cacheevictionlistener", factory = "hmppsQueueContainerFactoryProxy")
  fun onDomainEvent(
    rawMessage: String,
  ) {
    val sqsMessage: SQSMessage = objectMapper.readValue(rawMessage)
    return when (sqsMessage.Type) {
      "Notification" -> {
        processMessage(sqsMessage.Message)
      } else -> {}
    }
  }

  private fun processMessage(message: String) {
    val prisonerEvent = objectMapper.readValue<PrisonerEvent>(message)
    val additionalInformation = prisonerEvent.additionalInformation

    val prisonerIds = prisonIdFields.mapNotNull {
      if (additionalInformation.has(it)) {
        return@mapNotNull additionalInformation.get(it).asText()
      }
      return@mapNotNull null
    }

    if (prisonerIds.isEmpty()) {
      log.error("Unable to find prisoner ID from event ${prisonerEvent.eventType}")
    } else {
      log.info("Event ${prisonerEvent.eventType} triggers cache evict for prisoner IDs $prisonerIds")
    }

    prisonerIds.forEach { prisonerId ->
      ThingToDoType.entries.forEach {
        thingToDoCacheService.evictCache(it.name, prisonerId)
      }
    }
  }

  data class PrisonerEvent(
    val eventType: String,
    val additionalInformation: JsonNode,
  )
}
