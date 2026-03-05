package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.AdjustmentsApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.AdjustmentToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.AdaIntercept
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.AdjustmentThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.InterceptType

class AdjustmentsThingsToDoProviderTest {

  private val adjustmentsApiClient: AdjustmentsApiClient = mock()

  private val provider = AdjustmentsThingsToDoProvider(adjustmentsApiClient)

  @Test
  fun `Gets nothing to do`() {
    whenever(adjustmentsApiClient.thingsToDo(PRISONER_ID)).thenReturn(AdjustmentThingsToDo(PRISONER_ID))

    val thingsToDo = provider.getThingsToDo(PRISONER_ID, mutableListOf(), SERVICE_CONFIG)

    assertThat(thingsToDo).isEmpty()
  }

  @ParameterizedTest(name = "Get ADA intercept {0} with {1} days")
  @CsvSource(
    "NONE,1,false,Review adjustment information,Review ADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "FIRST_TIME,1,false,Review ADA adjudication,Review ADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "UPDATE,1,false,Review ADA updates,Review ADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "PADA,1,false,Review PADA,Review PADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "PADAS,1,false,Review PADA,Review PADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "FIRST_TIME_WITH_NO_ADJUDICATION,1,false,Review ADA adjudication,Review ADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "POTENTIAL,1,true,View ADAs (Additional Days Awarded),View Potential ADAs,http://localhost/adjustments/A1234BC/additional-days/review-prospective",
    "NONE,2,false,Review adjustment information,Review ADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "FIRST_TIME,2,false,Review ADA adjudications,Review ADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "UPDATE,2,false,Review ADA updates,Review ADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "PADA,2,false,Review PADAs,Review PADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "PADAS,2,false,Review PADAs,Review PADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "FIRST_TIME_WITH_NO_ADJUDICATION,2,false,Review ADA adjudications,Review ADA,http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
    "POTENTIAL,2,true,View ADAs (Additional Days Awarded),View Potential ADAs,http://localhost/adjustments/A1234BC/additional-days/review-prospective",
  )
  fun `Gets ADA intercept`(
    interceptType: InterceptType,
    number: Int,
    anyProspective: Boolean,
    expectedTitle: String,
    expectedButtonText: String,
    expectedUrl: String,
  ) {
    whenever(adjustmentsApiClient.thingsToDo(PRISONER_ID)).thenReturn(
      AdjustmentThingsToDo(
        PRISONER_ID,
        listOf(AdjustmentToDoType.ADA_INTERCEPT),
        adaIntercept = AdaIntercept(interceptType, number, anyProspective, emptyList(), "Some message"),
      ),
    )

    val thingsToDo = provider.getThingsToDo(PRISONER_ID, mutableListOf(), SERVICE_CONFIG)

    assertThat(thingsToDo).isEqualTo(
      listOf(
        ThingToDo(
          title = expectedTitle,
          message = "Some message",
          buttonText = expectedButtonText,
          buttonHref = expectedUrl,
          type = ThingToDoType.ADA_INTERCEPT,
        ),
      ),
    )
  }

  @Test
  fun `Do not return an ADA intercept if the intercept details are missing`() {
    whenever(adjustmentsApiClient.thingsToDo(PRISONER_ID)).thenReturn(
      AdjustmentThingsToDo(
        PRISONER_ID,
        listOf(AdjustmentToDoType.ADA_INTERCEPT),
        adaIntercept = null,
      ),
    )

    val thingsToDo = provider.getThingsToDo(PRISONER_ID, mutableListOf(), SERVICE_CONFIG)

    assertThat(thingsToDo).isEmpty()
  }

  @Test
  fun `Gets review previous UAL`() {
    whenever(adjustmentsApiClient.thingsToDo(PRISONER_ID)).thenReturn(
      AdjustmentThingsToDo(
        PRISONER_ID,
        listOf(AdjustmentToDoType.PREVIOUS_PERIOD_OF_UAL_FOR_REVIEW),
        adaIntercept = null,
      ),
    )

    val thingsToDo = provider.getThingsToDo(PRISONER_ID, mutableListOf(), SERVICE_CONFIG)

    assertThat(thingsToDo).isEqualTo(
      listOf(
        ThingToDo(
          title = "Review UAL",
          message = "There are some previous periods of UAL that may be relevant to the release dates calculation. Check whether this UAL needs to saved before calculating release dates.",
          buttonText = "Review UAL",
          buttonHref = "http://localhost/adjustments/A1234BC/review-previous-unlawfully-at-large-periods",
          type = ThingToDoType.PREVIOUS_PERIOD_OF_UAL_FOR_REVIEW,
        ),
      ),
    )
  }

  @Test
  fun `Gets ADA intercept and review previous UAL`() {
    whenever(adjustmentsApiClient.thingsToDo(PRISONER_ID)).thenReturn(
      AdjustmentThingsToDo(
        PRISONER_ID,
        listOf(AdjustmentToDoType.ADA_INTERCEPT, AdjustmentToDoType.PREVIOUS_PERIOD_OF_UAL_FOR_REVIEW),
        adaIntercept = AdaIntercept(InterceptType.PADA, 1, false, emptyList(), "Some message"),
      ),
    )

    val thingsToDo = provider.getThingsToDo(PRISONER_ID, mutableListOf(), SERVICE_CONFIG)

    assertThat(thingsToDo).isEqualTo(
      listOf(
        ThingToDo(
          title = "Review PADA",
          message = "Some message",
          buttonText = "Review PADA",
          buttonHref = "http://localhost/adjustments/A1234BC/additional-days/review-and-approve",
          type = ThingToDoType.ADA_INTERCEPT,
        ),
        ThingToDo(
          title = "Review UAL",
          message = "There are some previous periods of UAL that may be relevant to the release dates calculation. Check whether this UAL needs to saved before calculating release dates.",
          buttonText = "Review UAL",
          buttonHref = "http://localhost/adjustments/A1234BC/review-previous-unlawfully-at-large-periods",
          type = ThingToDoType.PREVIOUS_PERIOD_OF_UAL_FOR_REVIEW,
        ),
      ),
    )
  }

  companion object {
    private const val PRISONER_ID = "A1234BC"
    private val SERVICE_CONFIG = CcrdServiceConfig(
      uiUrl = "http://localhost/adjustments",
      urlMapping = "http://localhost/adjustments/{prisonerId}",
      requiredRoles = listOf("foo"),
      text = "adjustments",
    )
  }
}
