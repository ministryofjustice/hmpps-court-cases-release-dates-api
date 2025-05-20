package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.integration

import org.awaitility.kotlin.await
import org.awaitility.kotlin.matches
import org.awaitility.kotlin.untilCallTo
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest
import uk.gov.justice.hmpps.sqs.HmppsQueue
import uk.gov.justice.hmpps.sqs.HmppsQueueService
import uk.gov.justice.hmpps.sqs.MissingQueueException
import uk.gov.justice.hmpps.sqs.countMessagesOnQueue

class SqsIntegrationTestBase : IntegrationTestBase() {

  @Autowired
  private lateinit var hmppsQueueService: HmppsQueueService

  private val domainEventsTopic by lazy { hmppsQueueService.findByTopicId("domainevents") ?: throw MissingQueueException("HmppsTopic domainevents not found") }
  protected val domainEventsTopicSnsClient by lazy { domainEventsTopic.snsClient }
  protected val domainEventsTopicArn by lazy { domainEventsTopic.arn }

  protected val cacheEvictionQueue by lazy { hmppsQueueService.findByQueueId("cacheevictionlistener") as HmppsQueue }

  @BeforeEach
  fun cleanQueue() {
    await untilCallTo {
      cacheEvictionQueue.sqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(cacheEvictionQueue.queueUrl).build())
      cacheEvictionQueue.sqsClient.countMessagesOnQueue(cacheEvictionQueue.queueUrl).get()
    } matches { it == 0 }
  }

  companion object {
    private val localStackContainer = LocalStackContainer.instance

    @Suppress("unused")
    @JvmStatic
    @DynamicPropertySource
    fun testcontainers(registry: DynamicPropertyRegistry) {
      localStackContainer?.also { LocalStackContainer.setLocalStackProperties(it, registry) }
    }
  }
}
