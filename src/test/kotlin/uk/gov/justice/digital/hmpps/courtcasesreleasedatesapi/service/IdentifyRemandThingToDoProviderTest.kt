package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.client.IdentifyRemandApiClient
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CcrdServiceConfig
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.ThingToDoType
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.IdentifyRemandThingsToDo
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.model.external.ToDoType

class IdentifyRemandThingToDoProviderTest {
  private val identifyRemandApiClient: IdentifyRemandApiClient = mock()
  private val provider = IdentifyRemandThingToDoProvider(identifyRemandApiClient, "http://localhost/irtool")

  @Test
  fun `Gets nothing to do`() {
    whenever(identifyRemandApiClient.thingsToDo(PRISONER_ID)).thenReturn(IdentifyRemandThingsToDo(PRISONER_ID))

    val thingsToDo = provider.getThingsToDo(PRISONER_ID, mutableListOf(), SERVICE_CONFIG)

    assertThat(thingsToDo).isEmpty()
  }

  @Test
  fun `Gets no remand to apply and points to IR tool and not adjustments even though this is shown under adjustments tab`() {
    whenever(identifyRemandApiClient.thingsToDo(PRISONER_ID)).thenReturn(
      IdentifyRemandThingsToDo(
        PRISONER_ID,
        thingsToDo = listOf(
          ToDoType.IDENTIFY_REMAND_REVIEW_FIRST_TIME,
        ),
        days = null,
      ),
    )

    val thingsToDo = provider.getThingsToDo(PRISONER_ID, mutableListOf(), SERVICE_CONFIG)

    assertThat(thingsToDo).isEqualTo(
      listOf(
        ThingToDo(
          title = "The remand tool has calculated that there is no remand to be applied.",
          message = "Review the remand tool before calculating a release date.",
          buttonText = "Review remand",
          buttonHref = "http://localhost/irtool/prisoner/$PRISONER_ID",
          type = ThingToDoType.REVIEW_IDENTIFIED_REMAND,
        ),
      ),
    )
  }

  @Test
  fun `Gets remand to apply and points to IR tool and not adjustments even though this is shown under adjustments tab`() {
    whenever(identifyRemandApiClient.thingsToDo(PRISONER_ID)).thenReturn(
      IdentifyRemandThingsToDo(
        PRISONER_ID,
        thingsToDo = listOf(
          ToDoType.IDENTIFY_REMAND_REVIEW_FIRST_TIME,
        ),
        days = 10,
      ),
    )

    val thingsToDo = provider.getThingsToDo(PRISONER_ID, mutableListOf(), SERVICE_CONFIG)

    assertThat(thingsToDo).isEqualTo(
      listOf(
        ThingToDo(
          title = "The remand tool has calculated that there is relevant remand to be applied.",
          message = "Review the remand tool before calculating a release date.",
          buttonText = "Review remand",
          buttonHref = "http://localhost/irtool/prisoner/$PRISONER_ID",
          type = ThingToDoType.REVIEW_IDENTIFIED_REMAND,
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
