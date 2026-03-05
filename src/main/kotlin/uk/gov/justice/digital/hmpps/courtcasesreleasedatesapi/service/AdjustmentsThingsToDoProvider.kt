package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.AdjustmentsApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDoProviderName
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.InterceptType

@Component
class AdjustmentsThingsToDoProvider(
  private val adjustmentsApiClient: AdjustmentsApiClient,
) : ThingsToDoProvider {
  override val serviceName: String = "adjustments"

  override fun getThingsToDo(prisonerId: String, existingThingsToDo: MutableList<ThingsToDo>, serviceConfig: CcrdServiceConfig): List<ThingToDo> {
    val adjustmentTodos = adjustmentsApiClient.thingsToDo(prisonerId)
    if (adjustmentTodos.thingsToDo.isNotEmpty() && adjustmentTodos.adaIntercept != null) {
      val intercept = adjustmentTodos.adaIntercept
      val interceptType = if (listOf(InterceptType.PADA, InterceptType.PADAS).contains(intercept.type)) "PADA" else "ADA"
      val buttonText = if (intercept.type != InterceptType.POTENTIAL) {
        "Review $interceptType"
      } else {
        "View Potential ADAs"
      }
      val pluralisation = if (intercept.number > 1) "s" else ""
      val title = if (listOf(InterceptType.FIRST_TIME, InterceptType.FIRST_TIME_WITH_NO_ADJUDICATION).contains(intercept.type)) {
        "Review ADA adjudication$pluralisation"
      } else if (interceptType == "PADA") {
        "Review PADA$pluralisation"
      } else if (intercept.type == InterceptType.UPDATE) {
        "Review ADA updates"
      } else if (intercept.type == InterceptType.POTENTIAL) {
        "View ADAs (Additional Days Awarded)"
      } else {
        "Review adjustment information"
      }
      return listOf(
        ThingToDo(
          title = title,
          message = intercept.message,
          buttonText = buttonText,
          buttonHref = if (intercept.anyProspective) serviceConfig.uiUrl + "/$prisonerId/additional-days/review-prospective" else serviceConfig.uiUrl + "/$prisonerId/additional-days/review-and-approve",
          type = ThingToDoType.ADA_INTERCEPT,
        ),
      )
    }
    return emptyList()
  }

  override fun thingsToDoProviderName(): ThingsToDoProviderName = ThingsToDoProviderName.ADJUSTMENTS
}
