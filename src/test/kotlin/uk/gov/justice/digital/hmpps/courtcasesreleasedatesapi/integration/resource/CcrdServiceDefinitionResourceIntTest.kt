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
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR", "REMAND_AND_SENTENCING", "REMAND_IDENTIFIER", "RECALL_MAINTAINER"))
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
      adjustmentsApiMockServer.verifyNumberOfThingsToDoCalls(PRISONER_ID, 1)
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
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR"))
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
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR"))
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
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR"))
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
    fun `Should call identify remand things to do if user has adjustments and ir roles and have right banner for non 0 day remand `() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubAdaProspectiveThingsToDo(PRISONER_ID)
      calculateReleaseDatesApiMockServer.stubGetNoThingsTodo(PRISONER_ID)
      identifyRemandApiMockServer.stubFirstTimeReviewWithNon0DaysThingsToDo(PRISONER_ID)
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR", "REMAND_IDENTIFIER"))
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
                      "title": "The remand tool has calculated that there is relevant remand to be applied.",
                      "message": "Review the remand tool before calculating a release date.",
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

    @Test
    fun `Should call identify remand things to do if user has adjustments and ir roles and have right banner for 0 day remand `() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubAdaProspectiveThingsToDo(PRISONER_ID)
      calculateReleaseDatesApiMockServer.stubGetNoThingsTodo(PRISONER_ID)
      identifyRemandApiMockServer.stubFirstTimeReviewWith0DaysThingsToDo(PRISONER_ID)
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR", "REMAND_IDENTIFIER"))
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
                      "title": "The remand tool has calculated that there is no remand to be applied.",
                      "message": "Review the remand tool before calculating a release date.",
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

    @Test
    fun `Should call identify remand things to do if user has adjustments and ir roles and have right banner for Null day remand `() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubAdaProspectiveThingsToDo(PRISONER_ID)
      calculateReleaseDatesApiMockServer.stubGetNoThingsTodo(PRISONER_ID)
      identifyRemandApiMockServer.stubFirstTimeReviewWithNullDaysThingsToDo(PRISONER_ID)
      getServiceDefinitions(listOf("RELEASE_DATES_CALCULATOR", "REMAND_IDENTIFIER"))
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
                      "title": "The remand tool has calculated that there is no remand to be applied.",
                      "message": "Review the remand tool before calculating a release date.",
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

  private fun getServiceDefinitions(roles: List<String>) = webTestClient.get()
    .uri("/service-definitions/prisoner/$PRISONER_ID")
    .headers(setAuthorisation(roles = roles))
    .exchange()
    .expectStatus()
    .isOk

  companion object {
    private const val PRISONER_ID = "AB1234AB"
  }
}
