package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.RemandAndSentencingApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo

@Component
class RemandAndSentencingThingsToDoProvider(
  private val remandAndSentencingApiClient: RemandAndSentencingApiClient,
) : ThingsToDoProvider {
  override val serviceName: String = "courtCases"

  override fun getThingsToDo(
    prisonerId: String,
    existingThingsToDo: MutableList<ThingsToDo>,
    serviceConfig: CcrdServiceConfig,
  ): List<ThingToDo> {
    val thingsToDo = remandAndSentencingApiClient.thingsToDo(prisonerId)
    return thingsToDo.thingsToDo.map {
      ThingToDo(
        title = "Enter information from a new remand warrant",
        message = "A new remand warrant for ${thingsToDo.hearingThingsToDoData!!.courtCaseReference} has been added from Common Platform. Review and add information from the remand warrant.",
        buttonText = "Review remand warrant",
        buttonHref = serviceConfig.uiUrl + "/person/$prisonerId/review-new-documents/${thingsToDo.hearingThingsToDoData.hearingId}/landing",
        type = ThingToDoType.REMAND_WARRANT_NEW_COURT_CASE,
      )
    }
  }

  override fun additionalRoles(): List<String> = listOf("CCRD_DOCUMENTS")
}
