package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.CourtDataIngestionApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDoProviderName

@Component
class DocumentsThingsToDoProvider(
  private val courtDataIngestionApiClient: CourtDataIngestionApiClient,
) : ThingsToDoProvider {
  override val serviceName: String = "documents"

  override fun getThingsToDo(
    prisonerId: String,
    existingThingsToDo: MutableList<ThingsToDo>,
    serviceConfig: CcrdServiceConfig,
  ): List<ThingToDo> {
    val thingsToDo = courtDataIngestionApiClient.thingsToDo(prisonerId)
    return thingsToDo.thingsToDo.map {
      ThingToDo.notificationOnlyThingToDo(
        ThingToDoType.HMCTS_API_DOCUMENT_RECEIVED,
      )
    }
  }

  override fun thingsToDoProviderName(): ThingsToDoProviderName = ThingsToDoProviderName.CRDS
}
