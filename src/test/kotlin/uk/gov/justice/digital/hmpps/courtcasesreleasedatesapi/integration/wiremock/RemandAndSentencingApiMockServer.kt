package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.exactly
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

class RemandAndSentencingApiMockServer : WireMockServer(8095) {

  fun verifyNumberOfThingsToDoCalls(prisonerId: String, number: Int) {
    verify(exactly(number), getRequestedFor(urlEqualTo("/things-to-do/prisoner/$prisonerId")))
  }

  fun stubThingsToDoRemandWarrant(prisonerId: String) {
    stubFor(
      get("/things-to-do/prisoner/$prisonerId").willReturn(
        aResponse()
          .withHeader("Content-Type", "application/json")
          .withBody(
            """
            {
                "prisonerId": "$prisonerId",
                "thingsToDo": ["NEW_REMAND_WARRANT"],
                "hearingThingsToDoData": {
                  "hearingId": "60466893-a289-4ba9-be8e-c9377731472c",
                  "courtCaseReference": "ABC123"
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

class RemandAndSentencingApiExtension :
  BeforeAllCallback,
  AfterAllCallback,
  BeforeEachCallback {
  companion object {
    @JvmField
    val remandAndSentencingApiMockServer = RemandAndSentencingApiMockServer()
  }

  override fun beforeAll(context: ExtensionContext): Unit = remandAndSentencingApiMockServer.start()
  override fun beforeEach(context: ExtensionContext): Unit = remandAndSentencingApiMockServer.resetAll()
  override fun afterAll(context: ExtensionContext): Unit = remandAndSentencingApiMockServer.stop()
}
