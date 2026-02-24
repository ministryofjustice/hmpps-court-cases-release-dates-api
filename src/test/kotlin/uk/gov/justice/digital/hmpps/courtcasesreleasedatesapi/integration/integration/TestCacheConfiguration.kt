package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.integration

import org.springframework.boot.test.context.TestConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CacheConfiguration

@TestConfiguration
class TestCacheConfiguration(objectMapper: ObjectMapper) : CacheConfiguration(objectMapper, 60)
