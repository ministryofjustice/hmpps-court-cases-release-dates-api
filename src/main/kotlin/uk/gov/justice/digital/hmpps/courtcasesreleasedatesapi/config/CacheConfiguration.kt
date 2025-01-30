package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
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
class CacheConfiguration {

  @Value("\${cache.ttlMinutes.default}")
  var defaultCacheTtlMinutes: Long = 60

  private fun objectMapper(): ObjectMapper = ObjectMapper()
    .registerModule(Jdk8Module())
    .registerModule(JavaTimeModule())
    .registerKotlinModule()
    .apply {
      activateDefaultTyping(polymorphicTypeValidator, ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY)
    }
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
