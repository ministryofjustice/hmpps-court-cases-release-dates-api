package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.cache.annotation.EnableCaching
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.AdjustmentsApiExtension.Companion.adjustmentsApiMockServer
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.CalculateReleaseDatesApiExtension.Companion.calculateReleaseDatesApiMockServer
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.HmppsAuthApiExtension.Companion.hmppsAuth
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.IdentifyRemandApiExtension.Companion.identifyRemandApiMockServer

@EnableCaching
class ThingsToDoResourceIntTest : IntegrationTestBase() {

  @Nested
  @DisplayName("GET /service-definitions caching")
  inner class CachingTests {
    @Test
    fun `Should hit cache for things to do after first lookup and not after evicting cache`() {
      hmppsAuth.stubGrantToken()
      adjustmentsApiMockServer.stubGetEmptyThingsTodo(PRISONER_ID)
      calculateReleaseDatesApiMockServer.stubGetNoThingsTodo(PRISONER_ID)
      identifyRemandApiMockServer.stubGetEmptyThingsTodo(PRISONER_ID)
      getServiceDefinitions(
        listOf(
          "RELEASE_DATES_CALCULATOR",
          "REMAND_AND_SENTENCING",
          "ADJUSTMENTS_MAINTAINER",
          "REMAND_IDENTIFIER",
        ),
      )
      getServiceDefinitions(
        listOf(
          "RELEASE_DATES_CALCULATOR",
          "REMAND_AND_SENTENCING",
          "ADJUSTMENTS_MAINTAINER",
          "REMAND_IDENTIFIER",
        ),
      )

      evictCache()

      getServiceDefinitions(
        listOf(
          "RELEASE_DATES_CALCULATOR",
          "REMAND_AND_SENTENCING",
          "ADJUSTMENTS_MAINTAINER",
          "REMAND_IDENTIFIER",
        ),
      )
      // Looked up things to do three times, first lookup from API, second from cache third from API after eviction.
      adjustmentsApiMockServer.verifyNumberOfThingsToDoCalls(PRISONER_ID, 2)
      calculateReleaseDatesApiMockServer.verifyNumberOfThingsToDoCalls(PRISONER_ID, 2)
      identifyRemandApiMockServer.verifyNumberOfThingsToDoCalls(PRISONER_ID, 2)
    }
  }

  private fun getServiceDefinitions(roles: List<String>) =
    webTestClient.get()
      .uri("/service-definitions/prisoner/${PRISONER_ID}")
      .headers(setAuthorisation(roles = roles))
      .exchange()
      .expectStatus()
      .isOk

  private fun evictCache() =
    webTestClient.delete()
      .uri("/things-to-do/prisoner/${PRISONER_ID}/evict")
      .headers(setAuthorisation(roles = listOf("CCRD__THINGS_TO_DO_RW")))
      .exchange()
      .expectStatus()
      .isOk

  companion object {
    private const val PRISONER_ID = "AB1234AB"
  }
}
