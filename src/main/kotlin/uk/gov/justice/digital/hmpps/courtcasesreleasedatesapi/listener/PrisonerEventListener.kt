package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.listener

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service.StandardTelemetryEvent
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service.TelemetryService
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service.ThingToDoCacheService

@Service
class PrisonerEventListener(
  private val objectMapper: ObjectMapper,
  private val thingToDoCacheService: ThingToDoCacheService,
  private val telemetryService: TelemetryService,
) {

  private companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @SqsListener("cacheevictionlistener", factory = "hmppsQueueContainerFactoryProxy")
  fun onDomainEvent(
    rawMessage: String,
  ) {
    val sqsMessage: SQSMessage = objectMapper.readValue(rawMessage)
    return when (sqsMessage.Type) {
      "Notification" -> {
        processMessage(sqsMessage.Message)
      }
      else -> {}
    }
  }

  private fun processMessage(message: String) {
    val prisonerEvent = objectMapper.readValue<PrisonerEvent>(message)

    val prisonerIds = prisonerEvent.toPrisonerIds()
    if (prisonerIds.isEmpty()) {
      throw UnknownPrisonerIdException("Unable to find prisoner ID from event ${prisonerEvent.eventType}")
    } else {
      log.info("Event ${prisonerEvent.eventType} triggers cache evict for prisoner IDs $prisonerIds")
    }

    prisonerIds.forEach { prisonerId ->
      ThingToDoType.entries.forEach {
        thingToDoCacheService.evictCache(it.name, prisonerId)
        telemetryService.track(CacheEvictionTelemetry(prisonerId, prisonerEvent.eventType))
      }
    }
  }

  class UnknownPrisonerIdException(message: String) : Exception(message)

  private data class PrisonerEvent(
    val eventType: String,
    val additionalInformation: AdditionalInfoPrisonerIds,
    val personReference: EventPersonReference? = null,
  ) {
    fun toPrisonerIds() = setOfNotNull(personReference?.identifiers?.find { it.type == "NOMS" }?.value) + additionalInformation.toPrisonerIds()
  }

  private data class AdditionalInfoPrisonerIds(
    val nomsNumber: String? = null,
    val removedNomsNumber: String? = null,
    val movedFromNomsNumber: String? = null,
    val movedToNomsNumber: String? = null,
    val offenderNo: String? = null,
    val prisonerNumber: String? = null,
    val prisonerId: String? = null,
  ) {
    fun toPrisonerIds() = setOfNotNull(
      nomsNumber,
      removedNomsNumber,
      movedFromNomsNumber,
      movedToNomsNumber,
      offenderNo,
      prisonerNumber,
      prisonerId,
    )
  }

  private data class EventPersonReference(val identifiers: List<EventPersonReferenceIdentifiers>)

  private data class EventPersonReferenceIdentifiers(val type: String, val value: String)

  private data class CacheEvictionTelemetry(val prisonerId: String, val causedByEventType: String) : StandardTelemetryEvent("prisoner-cache-eviction") {
    override fun properties() = mapOf(
      "prisonerId" to prisonerId,
      "causedByEventType" to causedByEventType,
    )
  }
}
