package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.AdjustmentToDoType.ADA_INTERCEPT
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.CalculationToDoType.CALCULATION_REQUIRED
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.AdjustmentsApiExtension.Companion.adjustmentsApiMockServer
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.CalculateReleaseDatesApiExtension.Companion.calculateReleaseDatesApiMockServer
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.HmppsAuthApiExtension.Companion.hmppsAuth
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.AdaIntercept
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.AdjustmentThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.InterceptType.UPDATE
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo

class ThingsToDoResourceIntTest : IntegrationTestBase() {

  @Nested
  @DisplayName("GET /things-to-do")
  inner class PrisonerEndpoint {

    @Test
    fun `Should return ADA intercept adjustment things to do`() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubGetAdaUpdateThingsTodo(PRISONER_ID)
      calculateReleaseDatesApiMockServer.stubGetCalcRequiredThingsToDo(PRISONER_ID)
      val thingsToDo = getThingsToDo()
      assertThat(thingsToDo).isEqualTo(
        ThingsToDo(
          prisonerId = PRISONER_ID,
          adjustmentThingsToDo = ADJUSTMENT_THINGS_TO_DO,
          hasAdjustmentThingsToDo = true,
          hasCalculationThingsToDo = false,
        ),
      )
    }

    @Test
    fun `Should return Calculation things to do`() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubGetEmptyThingsTodo(PRISONER_ID)
      calculateReleaseDatesApiMockServer.stubGetCalcRequiredThingsToDo(PRISONER_ID)
      val thingsToDo = getThingsToDo()
      assertThat(thingsToDo).isEqualTo(
        ThingsToDo(
          prisonerId = PRISONER_ID,
          calculationThingsToDo = listOf(CALCULATION_REQUIRED),
          hasAdjustmentThingsToDo = false,
          hasCalculationThingsToDo = true,
        ),
      )
    }
  }

  private fun getThingsToDo() =
    webTestClient.get()
      .uri("/things-to-do/prisoner/${PRISONER_ID}")
      .headers(setAuthorisation(roles = listOf("ADJUSTMENTS__ADJUSTMENTS_RO")))
      .exchange()
      .expectStatus()
      .isOk
      .expectBody(ThingsToDo::class.java)
      .returnResult().responseBody

  companion object {
    private const val PRISONER_ID = "AB1234AB"
    private val ADJUSTMENT_THINGS_TO_DO = AdjustmentThingsToDo(
      prisonerId = PRISONER_ID,
      thingsToDo = listOf(ADA_INTERCEPT),
      adaIntercept = AdaIntercept(type = UPDATE, number = 1, anyProspective = false),
    )
  }
}
