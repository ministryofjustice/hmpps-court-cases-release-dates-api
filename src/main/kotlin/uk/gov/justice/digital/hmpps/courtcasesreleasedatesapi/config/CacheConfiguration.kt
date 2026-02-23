package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
@ConditionalOnProperty("things-to-do-caching.enabled", havingValue = "true")
class CacheConfiguration(
  @param:Value("\${cache.ttlMinutes.default:60}")
  val defaultCacheTtlMinutes: Long,
) {

  private fun getDefaultCacheConfiguration(): RedisCacheConfiguration = RedisCacheConfiguration
    .defaultCacheConfig()
    .serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer()))
    .serializeValuesWith(SerializationPair.fromSerializer(JacksonJsonRedisSerializer(Any::class.java)))

  @Bean
  fun cacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer? = RedisCacheManagerBuilderCustomizer { builder: RedisCacheManagerBuilder ->
    val defaultConfigWithRefDataTtl = getDefaultCacheConfiguration()
      .entryTtl(Duration.ofMinutes(defaultCacheTtlMinutes))

    arrayOf(
      CacheConstants.THINGS_TO_DO,
    ).forEach {
      builder.withCacheConfiguration(it, defaultConfigWithRefDataTtl)
    }
  }
}
