package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.AdjustmentsApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.CalculateReleaseDatesApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.AdjustmentToDoType.ADA_INTERCEPT
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.CalculationToDoType.CALCULATION_REQUIRED
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.AdaIntercept
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.AdjustmentThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.CalculationThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.InterceptType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo

class ThingsToDoServiceTest {

  private val adjustmentsApiClient = mock<AdjustmentsApiClient>()
  private val calculateReleaseDatesApiClient = mock<CalculateReleaseDatesApiClient>()

  private val thingsToDoService = ThingsToDoService(
    calculateReleaseDatesApiClient,
    adjustmentsApiClient,
  )

  @Test
  fun `Return empty object when there are no things to do`() {
    whenever(adjustmentsApiClient.thingsToDo(PRISONER_ID)).thenReturn(EMPTY_ADJUSTMENT_THINGS_TO_DO)
    whenever(calculateReleaseDatesApiClient.thingsToDo(PRISONER_ID)).thenReturn(EMPTY_CALCULATION_THINGS_TO_DO)

    val result = thingsToDoService.getToDoList(PRISONER_ID)

    assertThat(result).isEqualTo(
      ThingsToDo(
        prisonerId = PRISONER_ID,
      ),
    )
  }

  @Test
  fun `Only return adjustment things to do when there are things to do from adjustments and crd`() {
    whenever(adjustmentsApiClient.thingsToDo(PRISONER_ID)).thenReturn(ADJUSTMENT_THINGS_TO_DO)
    whenever(calculateReleaseDatesApiClient.thingsToDo(PRISONER_ID)).thenReturn(CALCULATION_THINGS_TO_DO)

    val result = thingsToDoService.getToDoList(PRISONER_ID)

    assertThat(result).isEqualTo(
      ThingsToDo(
        prisonerId = PRISONER_ID,
        adjustmentThingsToDo = ADJUSTMENT_THINGS_TO_DO,
        hasAdjustmentThingsToDo = true,
        hasCalculationThingsToDo = false,
      ),
    )
  }

  @Test
  fun `Return calculation things to do when there are no adjustments`() {
    whenever(adjustmentsApiClient.thingsToDo(PRISONER_ID)).thenReturn(EMPTY_ADJUSTMENT_THINGS_TO_DO)
    whenever(calculateReleaseDatesApiClient.thingsToDo(PRISONER_ID)).thenReturn(CALCULATION_THINGS_TO_DO)

    val result = thingsToDoService.getToDoList(PRISONER_ID)

    assertThat(result).isEqualTo(
      ThingsToDo(
        prisonerId = PRISONER_ID,
        calculationThingsToDo = listOf(CALCULATION_REQUIRED),
        hasAdjustmentThingsToDo = false,
        hasCalculationThingsToDo = true,
      ),
    )
  }

  companion object {
    private const val PRISONER_ID = "AB1234AB"
    private val EMPTY_ADJUSTMENT_THINGS_TO_DO = AdjustmentThingsToDo(prisonerId = PRISONER_ID)
    private val EMPTY_CALCULATION_THINGS_TO_DO = CalculationThingsToDo(prisonerId = PRISONER_ID)
    private val ADJUSTMENT_THINGS_TO_DO = AdjustmentThingsToDo(
      prisonerId = PRISONER_ID,
      thingsToDo = listOf(ADA_INTERCEPT),
      adaIntercept = AdaIntercept(type = InterceptType.UPDATE, number = 1, anyProspective = false, emptyList(), "message"),
    )
    private val CALCULATION_THINGS_TO_DO = CalculationThingsToDo(
      prisonerId = PRISONER_ID,
      thingsToDo = listOf(CALCULATION_REQUIRED),
    )
  }
}
