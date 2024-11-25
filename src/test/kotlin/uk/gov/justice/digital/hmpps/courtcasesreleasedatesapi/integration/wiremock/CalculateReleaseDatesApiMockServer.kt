package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class CalculateReleaseDatesApiMockServer : WireMockServer(8091) {
  fun stubGetCalcRequiredThingsToDo(prisonerId: String) {
    stubFor(
      get("/things-to-do/prisoner/$prisonerId")
        .willReturn(
          aResponse()
            .withHeader("Content-Type", "application/json")
            .withBody(
              """
              {
                  "prisonerId": "$prisonerId",
                  "thingsToDo": ["CALCULATION_REQUIRED"]
              }
              """.trimIndent(),
            ),
        ),
    )
  }

  fun stubGetNoThingsTodo(prisonerId: String) {
    stubFor(
      get("/things-to-do/prisoner/$prisonerId")
        .willReturn(
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

class CalculateReleaseDatesApiExtension : BeforeAllCallback, AfterAllCallback, BeforeEachCallback {
  companion object {
    @JvmField
    val calculateReleaseDatesApiMockServer = CalculateReleaseDatesApiMockServer()
  }

  override fun beforeAll(context: ExtensionContext): Unit = calculateReleaseDatesApiMockServer.start()
  override fun beforeEach(context: ExtensionContext): Unit = calculateReleaseDatesApiMockServer.resetAll()
  override fun afterAll(context: ExtensionContext): Unit = calculateReleaseDatesApiMockServer.stop()
}
