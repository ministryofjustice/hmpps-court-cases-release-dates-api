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
    fun `Should return services for user with all roles and no things to do`() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubGetEmptyThingsTodo(PRISONER_ID)
      calculateReleaseDatesApiMockServer.stubGetNoThingsTodo(PRISONER_ID)
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR", "REMAND_AND_SENTENCING", "ADJUSTMENTS_MAINTAINER"))
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
                  "count": 0
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
                    "things": [],
                    "count": 0
                  }
                },
                "releaseDates": {
                  "href": "http://localhost:8004?prisonId=AB1234AB",
                  "text": "Release dates and calculations",
                  "thingsToDo": {
                    "things": [
                      {
                        "title": "Calculation required",
                        "message": "Some information has changed. Check that all information is up to date then calculate release dates.",
                        "buttonText": "Calculate release dates",
                        "buttonHref": "http://localhost:8004/calculation/AB1234AB/reason",
                        "type": "CALCULATION_REQUIRED"
                      }
                    ],
                    "count": 1
                  }
                }
              }
            }
          """.trimIndent(),
        )
      adjustmentsApiMockServer.verifyNoThingsToDoCalls(PRISONER_ID)
    }
  }

  @Nested
  @DisplayName("GET /service-definitions variations of ADA intercept messages")
  inner class AdaMessageTests {

    @Test
    fun `Should return ADA update`() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubGetAdaUpdateThingsTodo(PRISONER_ID)
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR", "ADJUSTMENTS_MAINTAINER"))
        .expectBody()
        .json(
          """
          {
            "services": {
              "overview": {
                "href": "http://localhost:8000/prisoner/AB1234AB/overview",
                "text": "Overview",
                "thingsToDo": {
                  "things": [],
                  "count": 0
                }
              },
              "adjustments": {
                "href": "http://localhost:8002/AB1234AB",
                "text": "Adjustments",
                "thingsToDo": {
                  "things": [
                    {
                      "title": "Review ADA updates",
                      "message": "message",
                      "buttonText": "Review ADA",
                      "buttonHref": "http://localhost:8002/AB1234AB/additional-days/review-and-approve",
                      "type": "ADA_INTERCEPT"
                    }
                  ],
                  "count": 1
                }
              },
              "releaseDates": {
                "href": "http://localhost:8004?prisonId=AB1234AB",
                "text": "Release dates and calculations",
                "thingsToDo": {
                  "things": [],
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
    fun `Should return ADA first time`() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubAdaFirstTimeThingsToDo(PRISONER_ID)
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR", "ADJUSTMENTS_MAINTAINER"))
        .expectBody()
        .json(
          """
          {
            "services": {
              "overview": {
                "href": "http://localhost:8000/prisoner/AB1234AB/overview",
                "text": "Overview",
                "thingsToDo": {
                  "things": [],
                  "count": 0
                }
              },
              "adjustments": {
                "href": "http://localhost:8002/AB1234AB",
                "text": "Adjustments",
                "thingsToDo": {
                  "things": [
                    {
                      "title": "Review ADA adjudications",
                      "message": "message",
                      "buttonText": "Review ADA",
                      "buttonHref": "http://localhost:8002/AB1234AB/additional-days/review-and-approve",
                      "type": "ADA_INTERCEPT"
                    }
                  ],
                  "count": 1
                }
              },
              "releaseDates": {
                "href": "http://localhost:8004?prisonId=AB1234AB",
                "text": "Release dates and calculations",
                "thingsToDo": {
                  "things": [],
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
    fun `Should return ADA prospective`() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubAdaProspectiveThingsToDo(PRISONER_ID)
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR", "ADJUSTMENTS_MAINTAINER"))
        .expectBody()
        .json(
          """
          {
            "services": {
              "overview": {
                "href": "http://localhost:8000/prisoner/AB1234AB/overview",
                "text": "Overview",
                "thingsToDo": {
                  "things": [],
                  "count": 0
                }
              },
              "adjustments": {
                "href": "http://localhost:8002/AB1234AB",
                "text": "Adjustments",
                "thingsToDo": {
                  "things": [
                    {
                      "title": "Review PADA",
                      "message": "message",
                      "buttonText": "Review PADA",
                      "buttonHref": "http://localhost:8002/AB1234AB/additional-days/review-prospective",
                      "type": "ADA_INTERCEPT"
                    }
                  ],
                  "count": 1
                }
              },
              "releaseDates": {
                "href": "http://localhost:8004?prisonId=AB1234AB",
                "text": "Release dates and calculations",
                "thingsToDo": {
                  "things": [],
                  "count": 0
                }
              }
            }
          }
          """.trimIndent(),
        )
      calculateReleaseDatesApiMockServer.verifyNoThingsToDoCalls(PRISONER_ID)
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
