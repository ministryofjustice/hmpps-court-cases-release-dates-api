package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.wiremock

import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import redis.embedded.RedisServer

class RedisExtension : BeforeAllCallback, AfterAllCallback {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(RedisExtension::class.java)

    @JvmField
    val redisServer: RedisServer = RedisServer.newRedisServer().port(6380).soutListener {
      log.info(it)
    }.build()
  }

  override fun beforeAll(context: ExtensionContext) {
    redisServer.start()
  }

  override fun afterAll(context: ExtensionContext) {
    redisServer.stop()
  }
}
