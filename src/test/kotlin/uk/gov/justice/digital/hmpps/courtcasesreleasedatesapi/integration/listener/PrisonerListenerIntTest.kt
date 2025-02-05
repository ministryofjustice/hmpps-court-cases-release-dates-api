package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.listener

import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import software.amazon.awssdk.services.sns.model.MessageAttributeValue
import software.amazon.awssdk.services.sns.model.PublishRequest
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.integration.SqsIntegrationTestBase
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.integration.TestCacheConfiguration
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.AdjustmentsApiExtension.Companion.adjustmentsApiMockServer
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.CalculateReleaseDatesApiExtension.Companion.calculateReleaseDatesApiMockServer
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.HmppsAuthApiExtension.Companion.hmppsAuth
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock.IdentifyRemandApiExtension.Companion.identifyRemandApiMockServer
import uk.gov.justice.hmpps.sqs.countAllMessagesOnQueue

@Import(TestCacheConfiguration::class)
class PrisonerListenerIntTest : SqsIntegrationTestBase() {

  @BeforeEach
  fun setup() {
    evictCache()
  }

  @Test
  fun `Check event will evict cache`() {
    hmppsAuth.stubGrantToken()
    adjustmentsApiMockServer.stubGetEmptyThingsTodo(PRISONER_ID)
    calculateReleaseDatesApiMockServer.stubGetNoThingsTodo(PRISONER_ID)
    identifyRemandApiMockServer.stubGetEmptyThingsTodo(PRISONER_ID)
    val allRoles = listOf(
      "RELEASE_DATES_CALCULATOR",
      "REMAND_AND_SENTENCING",
      "ADJUSTMENTS_MAINTAINER",
      "REMAND_IDENTIFIER",
    )

    getServiceDefinitions(allRoles)
    getServiceDefinitions(allRoles)

    val eventType = "prisoner-offender-search.prisoner.received"
    domainEventsTopicSnsClient.publish(
      PublishRequest.builder().topicArn(domainEventsTopicArn)
        .message(prisonerAdmissionPayload(PRISONER_ID, eventType))
        .messageAttributes(
          mapOf(
            "eventType" to MessageAttributeValue.builder().dataType("String")
              .stringValue(eventType).build(),
          ),
        ).build(),
    ).get()

    await untilAsserted {
      Thread.sleep(100)
      assertThat(cacheEvictionQueue.sqsClient.countAllMessagesOnQueue(cacheEvictionQueue.queueUrl).get()).isEqualTo(0)
    }

    getServiceDefinitions(allRoles)

    // Looked up things to do three times, first lookup from API, second from cache third from API after event driven eviction.
    adjustmentsApiMockServer.verifyNumberOfThingsToDoCalls(PRISONER_ID, 2)
    calculateReleaseDatesApiMockServer.verifyNumberOfThingsToDoCalls(PRISONER_ID, 2)
    identifyRemandApiMockServer.verifyNumberOfThingsToDoCalls(PRISONER_ID, 2)
  }

  private fun getServiceDefinitions(roles: List<String>) =
    webTestClient.get()
      .uri("/service-definitions/prisoner/$PRISONER_ID")
      .headers(setAuthorisation(roles = roles))
      .exchange()
      .expectStatus()
      .isOk

  private fun evictCache() =
    webTestClient.delete()
      .uri("/things-to-do/prisoner/$PRISONER_ID/evict")
      .headers(setAuthorisation(roles = listOf("COURT_CASES_RELEASE_DATES__PRE_SENTENCE_CALC_REVIEW_TASKS__RW")))
      .exchange()
      .expectStatus()
      .isOk

  private fun prisonerAdmissionPayload(nomsNumber: String, eventType: String) =
    """{"eventType":"$eventType", "additionalInformation": {"nomsNumber":"$nomsNumber", "reason": "NEW_ADMISSION"}}"""

  companion object {
    private const val PRISONER_ID = "AB1234AB"
  }
}
