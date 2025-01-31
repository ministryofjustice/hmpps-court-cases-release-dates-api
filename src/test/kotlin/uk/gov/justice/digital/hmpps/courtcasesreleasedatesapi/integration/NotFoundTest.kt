package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration

import org.junit.jupiter.api.Test
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.integration.IntegrationTestBase
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.integration.SqsIntegrationTestBase

class NotFoundTest : SqsIntegrationTestBase() {

  @Test
  fun `Resources that aren't found should return 404 - test of the exception handler`() {
    webTestClient.get().uri("/some-url-not-found")
      .headers(setAuthorisation())
      .exchange()
      .expectStatus().isNotFound
  }
}
