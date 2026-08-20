package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.RemandAndSentencingApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.HearingThingsToDoWarrantType

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
      val warrantType = if (it.hearingThingsToDoData.warrantType == HearingThingsToDoWarrantType.SENTENCING) "sentencing" else "remand"
      ThingToDo(
        title = "Enter information from a new $warrantType warrant",
        message = """
          <p>A new $warrantType warrant has been added from Common Platform.</p>
          <p>This relates to <strong>${it.hearingThingsToDoData.courtCaseReference} heard on ${it.hearingThingsToDoData.hearingDate} (${it.hearingThingsToDoData.hearingType})</strong>.</p>
          <p>Review and add information from the warrant.</p>
        """.trimIndent(),
        messageIsHtml = true,
        buttonText = "Review $warrantType warrant",
        buttonHref = serviceConfig.uiUrl + "/person/$prisonerId/review-new-documents/${it.hearingThingsToDoData.hearingId}/landing" + if (it.hearingThingsToDoData.courtCaseUuid != null) "/existing-case" else "",
        type = ThingToDoType.NEW_HMCTS_WARRANT,
      )
    }
  }

  override fun additionalRoles(): List<String> = listOf("CCRD_DOCUMENTS", "RAS_DOCUMENT_AUTO")
}
