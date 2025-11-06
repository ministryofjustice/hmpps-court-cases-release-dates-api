package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.listener

import org.assertj.core.api.Assertions.assertThat
import org.awaitility.kotlin.await
import org.awaitility.kotlin.untilAsserted
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
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
import java.util.stream.Stream

@Import(TestCacheConfiguration::class)
class PrisonerListenerIntTest : SqsIntegrationTestBase() {

  @BeforeEach
  fun setup() {
    evictCache()
  }

  @ParameterizedTest
  @MethodSource("exampleEventPayloads")
  fun `Check event will evict cache`(eventType: String, additionalInfoJson: String, personReferenceJson: String?) {
    hmppsAuth.stubGrantToken()
    adjustmentsApiMockServer.stubGetEmptyThingsTodo(PRISONER_ID)
    calculateReleaseDatesApiMockServer.stubGetNoThingsTodo(PRISONER_ID)
    identifyRemandApiMockServer.stubGetEmptyThingsTodo(PRISONER_ID)
    val allRoles = listOf(
      "RELEASE_DATES_CALCULATOR",
      "REMAND_AND_SENTENCING",
      "REMAND_IDENTIFIER",
    )

    getServiceDefinitions(allRoles)
    getServiceDefinitions(allRoles)

    val payload =
      """{
        |"eventType":"$eventType", 
        |"additionalInformation": $additionalInfoJson
        |${personReferenceJson?.let { ", \"personReference\": $personReferenceJson" } ?: ""}
        |}
      """.trimMargin()

    domainEventsTopicSnsClient.publish(
      PublishRequest.builder().topicArn(domainEventsTopicArn)
        .message(payload)
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

  private fun getServiceDefinitions(roles: List<String>) = webTestClient.get()
    .uri("/service-definitions/prisoner/$PRISONER_ID")
    .headers(setAuthorisation(roles = roles))
    .exchange()
    .expectStatus()
    .isOk

  private fun evictCache() = webTestClient.delete()
    .uri("/things-to-do/prisoner/$PRISONER_ID/evict")
    .headers(setAuthorisation(roles = listOf("COURT_CASES_RELEASE_DATES__PRE_SENTENCE_CALC_REVIEW_TASKS__RW")))
    .exchange()
    .expectStatus()
    .isOk

  companion object {
    private const val PRISONER_ID = "AB1234AB"

    @JvmStatic
    fun exampleEventPayloads(): Stream<Arguments> = Stream.of(
      Arguments.of(
        "prisoner-offender-search.prisoner.updated",
        """{"nomsNumber":"$PRISONER_ID", "categoriesChanged": ["STATUS"]}""",
        null,
      ),
      Arguments.of(
        "prisoner-offender-search.prisoner.released",
        """{"nomsNumber":"$PRISONER_ID", "reason": "RELEASED"}""",
        null,
      ),
      Arguments.of(
        "prisoner-offender-search.prisoner.received",
        """{"nomsNumber":"$PRISONER_ID", "reason": "NEW_ADMISSION"}""",
        null,
      ),
      Arguments.of(
        "release-date-adjustments.adjustment.inserted",
        """{"offenderNo":"$PRISONER_ID", "unusedDeductions": false}""",
        null,
      ),
      Arguments.of(
        "release-date-adjustments.adjustment.updated",
        """{"offenderNo":"$PRISONER_ID", "unusedDeductions": false}""",
        null,
      ),
      Arguments.of(
        "release-date-adjustments.adjustment.deleted",
        """{"offenderNo":"$PRISONER_ID", "unusedDeductions": false}""",
        null,
      ),
      Arguments.of(
        "prison-offender-events.prisoner.merged",
        """{"nomsNumber":"$PRISONER_ID", "removedNomsNumber": "A9999ZZ", "reason": "MERGE"}""",
        null,
      ),
      Arguments.of(
        "prison-offender-events.prisoner.merged",
        """{"movedFromNomsNumber":"A9999ZZ", "movedToNomsNumber": "$PRISONER_ID", "reason": "MERGE"}""",
        null,
      ),
      Arguments.of(
        "prison-offender-events.prisoner.booking.moved",
        """{"movedFromNomsNumber":"$PRISONER_ID", "movedToNomsNumber": "A9999ZZ", "reason": "MERGE"}""",
        null,
      ),
      Arguments.of(
        "prison-offender-events.prisoner.booking.moved",
        """{"nomsNumber":"A9999ZZ", "removedNomsNumber": "$PRISONER_ID", "reason": "MERGE"}""",
        null,
      ),
      Arguments.of(
        "prison-offender-events.prisoner.sentence.changed",
        """{"nomsNumber": "$PRISONER_ID", "sentenceSequence": 1}""",
        null,
      ),
      Arguments.of(
        "prison-offender-events.prisoner.sentence.changed",
        """{"sentenceSequence": 1}""",
        """{ "identifiers": [{"type": "NOMS", "value": "$PRISONER_ID"}] }""",
      ),
      Arguments.of(
        "prison-offender-events.prisoner.sentence-term.changed",
        """{"nomsNumber": "$PRISONER_ID", "sentenceSequence": 1, "termSequence": 1}""",
        null,
      ),
      Arguments.of(
        "prison-offender-events.prisoner.sentence-term.changed",
        """{"sentenceSequence": 1, "termSequence": 1}""",
        """{ "identifiers": [{"type": "NOMS", "value": "$PRISONER_ID"}] }""",
      ),
      Arguments.of(
        "prison-offender-events.prisoner.fine-payment.changed",
        """{"nomsNumber": "$PRISONER_ID", "bookingId": 1}""",
        null,
      ),
      Arguments.of(
        "prison-offender-events.prisoner.fine-payment.changed",
        """{"bookingId": 1}""",
        """{ "identifiers": [{"type": "NOMS", "value": "$PRISONER_ID"}] }""",
      ),
      Arguments.of(
        "prison-offender-events.prisoner.fixed-term-recall.changed",
        """{"nomsNumber": "$PRISONER_ID", "bookingId": 1}""",
        null,
      ),
      Arguments.of(
        "prison-offender-events.prisoner.fixed-term-recall.changed",
        """{"bookingId": 1}""",
        """{ "identifiers": [{"type": "NOMS", "value": "$PRISONER_ID"}] }""",
      ),
      Arguments.of(
        "adjudication.punishments.created",
        """{"prisonerNumber": "$PRISONER_ID", "prisonId": "KMI", "chargeNumber": "1a"}""",
        null,
      ),
      Arguments.of(
        "adjudication.punishments.updated",
        """{"prisonerNumber": "$PRISONER_ID", "prisonId": "KMI", "chargeNumber": "1a"}""",
        null,
      ),
      Arguments.of(
        "adjudication.punishments.deleted",
        """{"prisonerNumber": "$PRISONER_ID", "prisonId": "KMI", "chargeNumber": "1a"}""",
        null,
      ),
      Arguments.of(
        "calculate-release-dates.prisoner.changed",
        """{"prisonerId": "$PRISONER_ID", "bookingId": 10}""",
        null,
      ),
    )
  }
}
