package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager.RedisCacheManagerBuilder
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

@Configuration
@EnableCaching
@ConditionalOnProperty("things-to-do-caching.enabled", havingValue = "true")
class CacheConfiguration(
  private val objectMapper: ObjectMapper,
  @Value("\${cache.ttlMinutes.default:60}")
  val defaultCacheTtlMinutes: Long,
) {

  private fun objectMapper(): ObjectMapper = objectMapper.copy()
    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

  private fun getDefaultCacheConfiguration(): RedisCacheConfiguration = RedisCacheConfiguration
    .defaultCacheConfig()
    .serializeKeysWith(SerializationPair.fromSerializer(StringRedisSerializer()))
    .serializeValuesWith(SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer(objectMapper())))

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
