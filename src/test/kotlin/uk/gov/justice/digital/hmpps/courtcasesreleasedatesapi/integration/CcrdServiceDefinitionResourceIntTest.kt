package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.AdjustmentsApiExtension.Companion.adjustmentsApiMockServer
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.CalculateReleaseDatesApiExtension.Companion.calculateReleaseDatesApiMockServer
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.HmppsAuthApiExtension.Companion.hmppsAuth

class CcrdServiceDefinitionResourceIntTest : IntegrationTestBase() {

  @Nested
  @DisplayName("GET /service-definitions")
  inner class PrisonerEndpoint {

    @Test
    fun `Should return services for user with all roles`() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubGetAdaUpdateThingsTodo(PRISONER_ID)
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR", "REMAND_AND_SENTENCING", "ADJUSTMENTS_MAINTAINER", "TODO"))
        .expectBody()
        .json(
          """
          {
            "services": {
              "overview": {
                "href": "http://localhost:8000/prisoner/AB1234AB/overview",
                "text": "Overview",
                "thingsToDo": {
                  "count": 0
                }
              },
              "courtCases": {
                "href": "http://localhost:8001/person/AB1234AB",
                "text": "Court cases",
                "thingsToDo": {
                  "count": 0
                }
              },
              "adjustments": {
                "href": "http://localhost:8002/AB1234AB",
                "text": "Adjustments",
                "thingsToDo": {
                  "count": 1,
                  "types": [
                    "ADA_INTERCEPT"
                  ],
                  "adaIntercept": {
                    "type": "UPDATE",
                    "number": 1,
                    "anyProspective": false,
                    "messageArguments": [],
                    "message": "message"
                  }
                }
              },
              "recalls": {
                "href": "http://localhost:8003/person/AB1234AB",
                "text": "Recalls",
                "thingsToDo": {
                  "count": 0
                }
              },
              "releaseDates": {
                "href": "http://localhost:8004?prisonId=AB1234AB",
                "text": "Release dates and calculations",
                "thingsToDo": {
                  "count": 0
                }
              }
            }
          }          
          """.trimIndent(),
        )
      calculateReleaseDatesApiMockServer.verifyNoThingsToDoCalls(PRISONER_ID)
    }

    @Test
    fun `Should return services for only crds role`() {
      hmppsAuth.stubGrantToken()
      calculateReleaseDatesApiMockServer.stubGetCalcRequiredThingsToDo(PRISONER_ID)
      getServiceDefinitions(
        listOf(
          "RELEASE_DATES_CALCULATOR",
        ),
      )
        .expectBody()
        .json(
          """
          {
            "services": {
              "overview": {
                "href": "http://localhost:8000/prisoner/AB1234AB/overview",
                "text": "Overview",
                "thingsToDo": {
                  "count": 0
                }
              },
              "releaseDates": {
                "href": "http://localhost:8004?prisonId=AB1234AB",
                "text": "Release dates and calculations",
                "thingsToDo": {
                  "count": 1,
                  "type": "CALCULATION_REQUIRED"
                }
              }
            }
          }
          """.trimIndent(),
        )
      adjustmentsApiMockServer.verifyNoThingsToDoCalls(PRISONER_ID)
    }
  }

  private fun getServiceDefinitions(roles: List<String>) =
    webTestClient.get()
      .uri("/service-definitions/prisoner/${PRISONER_ID}")
      .headers(setAuthorisation(roles = roles))
      .exchange()
      .expectStatus()
      .isOk

  companion object {
    private const val PRISONER_ID = "AB1234AB"
  }
}
