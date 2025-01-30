package uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.service

import org.springframework.cache.annotation.CacheEvict
import org.springframework.stereotype.Service
import uk.gov.justice.digital.hmpps.courtcasesreleasedatesapi.config.CacheConstants

@Service
class ThingToDoCacheService {

  @CacheEvict(CacheConstants.THINGS_TO_DO, key = "#thingToDoType.concat('-').concat(#prisonerId)")
  fun evictCache(thingToDoType: String, prisonerId: String) {}
}
