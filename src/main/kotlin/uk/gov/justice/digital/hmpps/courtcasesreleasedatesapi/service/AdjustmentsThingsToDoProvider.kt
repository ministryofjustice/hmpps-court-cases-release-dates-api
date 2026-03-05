package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.stereotype.Component
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.AdjustmentsApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.AdjustmentToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDoProviderName
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.AdaIntercept
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.InterceptType

@Component
class AdjustmentsThingsToDoProvider(
  private val adjustmentsApiClient: AdjustmentsApiClient,
) : ThingsToDoProvider {
  override val serviceName: String = "adjustments"

  override fun getThingsToDo(
    prisonerId: String,
    existingThingsToDo: MutableList<ThingsToDo>,
    serviceConfig: CcrdServiceConfig,
  ): List<ThingToDo> {
    val adjustmentTodos = adjustmentsApiClient.thingsToDo(prisonerId)
    return adjustmentTodos.thingsToDo.mapNotNull { type ->
      when (type) {
        AdjustmentToDoType.ADA_INTERCEPT -> adjustmentTodos.adaIntercept?.let {
          mapAdaIntercept(adjustmentTodos.adaIntercept, serviceConfig, prisonerId)
        }
        AdjustmentToDoType.PREVIOUS_PERIOD_OF_UAL_FOR_REVIEW -> mapPreviousUalForReview(serviceConfig, prisonerId)
      }
    }
  }

  private fun mapAdaIntercept(
    intercept: AdaIntercept,
    serviceConfig: CcrdServiceConfig,
    prisonerId: String,
  ): ThingToDo {
    val interceptType = if (listOf(InterceptType.PADA, InterceptType.PADAS).contains(intercept.type)) "PADA" else "ADA"
    val buttonText = if (intercept.type != InterceptType.POTENTIAL) {
      "Review $interceptType"
    } else {
      "View Potential ADAs"
    }
    val pluralisation = if (intercept.number > 1) "s" else ""
    val title =
      if (listOf(InterceptType.FIRST_TIME, InterceptType.FIRST_TIME_WITH_NO_ADJUDICATION).contains(intercept.type)) {
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
    return ThingToDo(
      title = title,
      message = intercept.message,
      buttonText = buttonText,
      buttonHref = if (intercept.anyProspective) serviceConfig.uiUrl + "/$prisonerId/additional-days/review-prospective" else serviceConfig.uiUrl + "/$prisonerId/additional-days/review-and-approve",
      type = ThingToDoType.ADA_INTERCEPT,
    )
  }

  private fun mapPreviousUalForReview(
    serviceConfig: CcrdServiceConfig,
    prisonerId: String,
  ): ThingToDo = ThingToDo(
    title = "Review UAL",
    message = "There are some previous periods of UAL that may be relevant to the release dates calculation. Check whether this UAL needs to saved before calculating release dates.",
    buttonText = "Review UAL",
    buttonHref = "${serviceConfig.uiUrl}/$prisonerId/review-previous-unlawfully-at-large-periods",
    type = ThingToDoType.PREVIOUS_PERIOD_OF_UAL_FOR_REVIEW,
  )

  override fun thingsToDoProviderName(): ThingsToDoProviderName = ThingsToDoProviderName.ADJUSTMENTS
}
