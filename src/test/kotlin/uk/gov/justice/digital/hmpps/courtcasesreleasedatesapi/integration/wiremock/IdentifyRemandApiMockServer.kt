package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.exactly
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class IdentifyRemandApiMockServer : WireMockServer(8093) {

  fun verifyNoThingsToDoCalls(prisonerId: String) {
    verify(exactly(0), getRequestedFor(urlEqualTo("/things-to-do/prisoner/$prisonerId")))
  }

  fun stubFirstTimeReviewThingsToDo(prisonerId: String) {
    stubFor(
      get("/things-to-do/prisoner/$prisonerId").willReturn(
        aResponse()
          .withHeader("Content-Type", "application/json")
          .withBody(
            """
            {
                "prisonerId": "$prisonerId",
                "thingsToDo": ["IDENTIFY_REMAND_REVIEW_FIRST_TIME"],
                "days": 10
            }
            """.trimIndent(),
          ),
      ),
    )
  }

  fun stubGetEmptyThingsTodo(prisonerId: String) {
    stubFor(
      get("/things-to-do/prisoner/$prisonerId").willReturn(
        aResponse()
          .withHeader("Content-Type", "application/json")
          .withBody(
            """
            {
                "prisonerId": "$prisonerId"
            }
            """.trimIndent(),
          ),
      ),
    )
  }
}

class IdentifyRemandApiExtension : BeforeAllCallback, AfterAllCallback, BeforeEachCallback {
  companion object {
    @JvmField
    val identifyRemandApiMockServer = IdentifyRemandApiMockServer()
  }

  override fun beforeAll(context: ExtensionContext): Unit = identifyRemandApiMockServer.start()
  override fun beforeEach(context: ExtensionContext): Unit = identifyRemandApiMockServer.resetAll()
  override fun afterAll(context: ExtensionContext): Unit = identifyRemandApiMockServer.stop()
}
