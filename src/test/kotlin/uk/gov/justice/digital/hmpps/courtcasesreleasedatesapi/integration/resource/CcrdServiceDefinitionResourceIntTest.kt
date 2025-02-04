package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.resource

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.integration.SqsIntegrationTestBase
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.AdjustmentsApiExtension.Companion.adjustmentsApiMockServer
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.CalculateReleaseDatesApiExtension.Companion.calculateReleaseDatesApiMockServer
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.HmppsAuthApiExtension.Companion.hmppsAuth
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.IdentifyRemandApiExtension.Companion.identifyRemandApiMockServer

class CcrdServiceDefinitionResourceIntTest : SqsIntegrationTestBase() {

  @Nested
  @DisplayName("GET /service-definitions")
  inner class PrisonerEndpoint {
    @Test
    fun `Should return services for user with all roles and no things to do`() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubGetEmptyThingsTodo(PRISONER_ID)
      calculateReleaseDatesApiMockServer.stubGetNoThingsTodo(PRISONER_ID)
      identifyRemandApiMockServer.stubGetEmptyThingsTodo(PRISONER_ID)
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR", "REMAND_AND_SENTENCING", "ADJUSTMENTS_MAINTAINER", "REMAND_IDENTIFIER"))
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
      identifyRemandApiMockServer.verifyNoThingsToDoCalls(PRISONER_ID)
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

  @Nested
  @DisplayName("GET /service-definitions identify remand things to do")
  inner class IdentifyRemandThingsToDo {
    @Test
    fun `Should call identify remand things to do if user has adjustments and ir roles`() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubAdaProspectiveThingsToDo(PRISONER_ID)
      calculateReleaseDatesApiMockServer.stubGetNoThingsTodo(PRISONER_ID)
      identifyRemandApiMockServer.stubFirstTimeReviewThingsToDo(PRISONER_ID)
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR", "ADJUSTMENTS_MAINTAINER", "REMAND_IDENTIFIER"))
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
                    },
                    {
                      "title": "There are periods of remand to review",
                      "message": "This service has identified periods of remand that may be relevant. You must review this remand periods before calculating a release date.",
                      "buttonText": "Review remand",
                      "buttonHref": "http://localhost:8005/prisoner/AB1234AB",
                      "type": "REVIEW_IDENTIFIED_REMAND"
                    }
                  ],
                  "count": 2
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
    }
  }

  private fun getServiceDefinitions(roles: List<String>) =
    webTestClient.get()
      .uri("/service-definitions/prisoner/$PRISONER_ID")
      .headers(setAuthorisation(roles = roles))
      .exchange()
      .expectStatus()
      .isOk

  companion object {
    private const val PRISONER_ID = "AB1234AB"
  }
}
