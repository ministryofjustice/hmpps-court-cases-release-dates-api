package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class AdjustmentsApiMockServer : WireMockServer(8092) {
  fun stubGetAdaUpdateThingsTodo(prisonerId: String) {
    stubFor(
      get("/things-to-do/prisoner/$prisonerId").willReturn(
        aResponse()
          .withHeader("Content-Type", "application/json")
          .withBody(
            """
            {
                "prisonerId": "$prisonerId",
                "thingsToDo": ["ADA_INTERCEPT"],
                "adaIntercept": {
                  "type": "UPDATE",
                  "number": "1",
                  "anyProspective": "false",
                  "message": "message"
                }
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

class AdjustmentsApiExtension : BeforeAllCallback, AfterAllCallback, BeforeEachCallback {
  companion object {
    @JvmField
    val adjustmentsApiMockServer = AdjustmentsApiMockServer()
  }

  override fun beforeAll(context: ExtensionContext): Unit = adjustmentsApiMockServer.start()
  override fun beforeEach(context: ExtensionContext): Unit = adjustmentsApiMockServer.resetAll()
  override fun afterAll(context: ExtensionContext): Unit = adjustmentsApiMockServer.stop()
}
