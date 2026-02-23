package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.integration.integration

import org.springframework.boot.test.context.TestConfiguration
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CacheConfiguration

@TestConfiguration
class TestCacheConfiguration : CacheConfiguration(60)
