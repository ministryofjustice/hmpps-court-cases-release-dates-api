package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.CalculateReleaseDatesApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.enums.CalculationToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.CalculationThingsToDo

class CrdsThingsToDoProviderTest {
  private val calculateReleaseDatesApiClient: CalculateReleaseDatesApiClient = mock()
  private val crdsThingsToDoProvider = CrdsThingsToDoProvider(calculateReleaseDatesApiClient)

  @Test
  fun `Gets nothing to do`() {
    whenever(calculateReleaseDatesApiClient.thingsToDo(PRISONER_ID)).thenReturn(CalculationThingsToDo(PRISONER_ID))

    val thingsToDo = crdsThingsToDoProvider.getThingsToDo(
      PRISONER_ID,
      mutableListOf(),
      SERVICE_CONFIG,
    )

    assertThat(thingsToDo).isEmpty()
    verify(calculateReleaseDatesApiClient).thingsToDo(PRISONER_ID)
  }

  @Test
  fun `Gets a calculation to do`() {
    whenever(calculateReleaseDatesApiClient.thingsToDo(PRISONER_ID)).thenReturn(
      CalculationThingsToDo(
        PRISONER_ID,
        thingsToDo = listOf(
          CalculationToDoType.CALCULATION_REQUIRED,
        ),
      ),
    )

    val thingsToDo = crdsThingsToDoProvider.getThingsToDo(
      PRISONER_ID,
      mutableListOf(),
      SERVICE_CONFIG,
    )

    assertThat(thingsToDo).isEqualTo(
      listOf(
        ThingToDo(
          title = "Calculation required",
          message = "Some information has changed. Check that all information is up to date then calculate release dates.",
          buttonText = "Calculate release dates",
          buttonHref = "http://localhost/crds/calculation/$PRISONER_ID/reason",
          type = ThingToDoType.CALCULATION_REQUIRED,
        ),

      ),
    )
    verify(calculateReleaseDatesApiClient).thingsToDo(PRISONER_ID)
  }

  @Test
  fun `Skips the check when there are existing things to do`() {
    whenever(calculateReleaseDatesApiClient.thingsToDo(PRISONER_ID)).thenReturn(
      CalculationThingsToDo(
        PRISONER_ID,
        thingsToDo = listOf(
          CalculationToDoType.CALCULATION_REQUIRED,
        ),
      ),
    )

    val thingsToDo = crdsThingsToDoProvider.getThingsToDo(
      PRISONER_ID,
      mutableListOf(
        ThingsToDo(
          listOf(
            ThingToDo(
              title = "ADA intercept",
              message = "foo",
              buttonText = "bar",
              buttonHref = "http://localhost/adjustments",
              type = ThingToDoType.ADA_INTERCEPT,
            ),
          ),
        ),
      ),
      SERVICE_CONFIG,
    )

    assertThat(thingsToDo).isEmpty()
    verify(calculateReleaseDatesApiClient, never()).thingsToDo(PRISONER_ID)
  }

  companion object {
    private const val PRISONER_ID = "A1234BC"
    private val SERVICE_CONFIG = CcrdServiceConfig(
      uiUrl = "http://localhost/crds",
      urlMapping = "http://localhost/crds?prisonId={prisonerId}",
      requiredRoles = listOf("CALCULATE_RELEASE_DATES"),
      text = "CRDS",
    )
  }
}
